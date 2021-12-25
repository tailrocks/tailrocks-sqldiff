package com.tailrocks.sqldiff.core.postgres;

import com.tailrocks.sqldiff.core.SqlClient;
import com.tailrocks.sqldiff.core.postgres.model.PgColumn;
import com.tailrocks.sqldiff.core.postgres.model.PgColumnType;
import com.tailrocks.sqldiff.core.postgres.model.PgConstraintType;
import com.tailrocks.sqldiff.core.postgres.model.PgEnum;
import com.tailrocks.sqldiff.core.postgres.model.PgExtension;
import com.tailrocks.sqldiff.core.postgres.model.PgIndex;
import com.tailrocks.sqldiff.core.postgres.model.PgSchema;
import com.tailrocks.sqldiff.core.postgres.model.PgSequence;
import com.tailrocks.sqldiff.core.postgres.model.PgTable;
import com.tailrocks.sqldiff.core.postgres.model.PgView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static com.tailrocks.sqldiff.core.util.KrendelUtils.getQuery;

public class SchemaReader {

    private static final Logger log = LoggerFactory.getLogger(SchemaReader.class.getSimpleName());

    private final SqlClient sqlClient;

    public SchemaReader(String jdbcUrl, String username, String password) {
        this.sqlClient = new SqlClient(jdbcUrl, username, password);
    }

    public PgSchema read(String schemaName) {
        PgSchema schema = new PgSchema(schemaName);

        readExtensions(schema);
        readEnums(schema);
        readSequences(schema);
        readTables(schema);
        readViews(schema);
        readColumns(schema);
        readSequencesDependencies(schema);
        readIndexes(schema);
        readConstraints(schema);
        readForeignKeys(schema);

        return schema;
    }

    private void readExtensions(PgSchema schema) {
        String query = getQuery("readExtensions.sql");

        sqlClient.executeQuery(String.format(query, schema.getName()), (ResultSet rs) -> {
            String extensionName = rs.getString("extensionname").toLowerCase();

            PgExtension extension = new PgExtension(extensionName);

            schema.addExtension(extension);

            log.debug("Found extension: {}", extensionName);
        });
    }

    private void readEnums(PgSchema schema) {
        Map<Integer, PgEnum> enumMap = new HashMap<>();

        String query = getQuery("readEnums.sql");

        sqlClient.executeQuery(String.format(query, schema.getName()), (ResultSet rs) -> {
            int oid = rs.getInt("enumtypid");
            String enumName = rs.getString("enum_name").toLowerCase();

            enumMap.put(oid, new PgEnum(oid, enumName));

            log.debug("Found enum: {}", enumName);
        });

        // TODO check for schema
        query = getQuery("readEnums_labels.sql");

        sqlClient.executeQuery(query, (ResultSet rs) -> {
            int oid = rs.getInt("enumtypid");
            String enumValue = rs.getString("enumlabel");

            enumMap.get(oid).addValue(enumValue);
        });

        schema.addEnums(enumMap.values());
    }

    private void readTables(PgSchema schema) {
        String query = getQuery("readTables.sql");

        sqlClient.executeQuery(String.format(query, schema.getName()), (ResultSet rs) -> {
            String tableName = rs.getString("tablename").toLowerCase();
            String relKind = rs.getString("relkind");
            String tableDescription = rs.getString("table_description");
            boolean isPartition = rs.getBoolean("relispartition");
            String partitionKeyDefinition = rs.getString("partitionkeydef");
            String partitionBound = rs.getString("partitionbound");

            PgTable table = schema.addTable(tableName);
            table.setDescription(tableDescription);
            table.setRelKind(relKind);
            table.setPartition(isPartition);
            table.setPartitionKeyDefinition(partitionKeyDefinition);
            table.setPartitionBound(partitionBound);

            log.debug("Found table: {}", tableName);
        });

        query = getQuery("readTables_relations.sql");

        sqlClient.executeQuery(String.format(query, schema.getName()), (ResultSet rs) -> {
            String parentTableName = rs.getString("parent").toLowerCase();
            String childTableName = rs.getString("child").toLowerCase();

            PgTable parentTable = schema.getTable(parentTableName);
            if (parentTable == null) {
                throw new IllegalStateException("Parent table " + parentTableName + " not found in schema " + schema.getName());
            }

            PgTable childTable = schema.getTable(childTableName);
            if (childTable == null) {
                throw new IllegalStateException("Child table " + childTableName + " not found in schema " + schema.getName());
            }

            parentTable.addChild(childTable);

            log.debug("Found children: {} > {}", childTableName, parentTableName);
        });
    }

    private void readColumns(PgSchema schema) {
        String query = getQuery("readColumns.sql");

        sqlClient.executeQuery(String.format(query, schema.getName()), (ResultSet rs) -> {
            String tableName = rs.getString("table_name").toLowerCase();
            String columnName = rs.getString("column_name").toLowerCase();
            String columnDefault = rs.getString("column_default");
            boolean isNullable = rs.getString("is_nullable").equals("YES");
            String columnDescription = rs.getString("column_description");
            String dataType = rs.getString("data_type");
            String udtName = rs.getString("udt_name");
            Integer characterMaximumLength = rs.getInt("character_maximum_length");
            Integer numericPrecision = rs.getInt("numeric_precision");
            Integer numericScale = rs.getInt("numeric_scale");

            PgTable table = schema.getTable(tableName);

            if (table == null) {
                PgView view = schema.getView(tableName);

                if (view == null) {
                    log.warn("Ignore column {}.{}, related table or view not found", tableName, columnName);
                    return;
                }

                // view's columns are not supported yet
                return;
            }

            PgColumnType columnType = new PgColumnType();
            columnType.setDataType(dataType);
            columnType.setUdtName(udtName);
            columnType.setCharacterMaximumLength(characterMaximumLength);
            columnType.setNumericPrecision(numericPrecision);
            columnType.setNumericScale(numericScale);

            PgColumn column = new PgColumn(columnName, columnType);
            column.setDefaultValue(columnDefault);
            column.setNullable(isNullable);
            column.setDescription(columnDescription);

            table.addColumn(column);

            log.debug("Found column: {}", columnName);

            if (columnDefault != null && columnDefault.toLowerCase().contains("nextval('")) {
                String sequenceName = columnDefault
                        .replace("nextval('", "")
                        .replace("'::regclass)", "")
                        .trim()
                        .toLowerCase();

                PgSequence sequence = schema.getSequence(sequenceName);

                if (sequence == null) {
                    throw new IllegalStateException("Read column " + tableName + "." + columnName + " failed: Sequence " + sequenceName + " not found in schema " + schema.getName());
                }

                column.setSequence(sequence);
            }
        });
    }

    private void readSequences(PgSchema schema) {
        String query = getQuery("readSequences.sql");

        sqlClient.executeQuery(String.format(query, schema.getName()), (ResultSet rs) -> {
            String sequenceName = rs.getString("sequencename").toLowerCase();
            String dataType = rs.getString("data_type");
            long startValue = rs.getLong("start_value");
            long minValue = rs.getLong("min_value");
            long maxValue = rs.getLong("max_value");
            long incrementBy = rs.getLong("increment_by");
            long cacheSize = rs.getLong("cache_size");

            PgSequence sequence = new PgSequence(sequenceName, dataType, startValue, minValue, maxValue, incrementBy,
                    cacheSize);

            schema.addSequence(sequence);

            log.debug("Found sequence: {}", sequence.getName());
        });
    }

    private void readSequencesDependencies(PgSchema schema) {
        String query = getQuery("readSequencesDependencies.sql");

        sqlClient.executeQuery(query, (ResultSet rs) -> {
            String sequenceName = rs.getString("sequencename").toLowerCase();
            String tableName = rs.getString("tablename").toLowerCase();
            String columnName = rs.getString("columnname").toLowerCase();
            String type = rs.getString("type");

            PgSequence sequence = schema.getSequence(sequenceName);
            if (sequence == null) {
                throw new IllegalStateException("Sequence " + sequenceName + " not found in schema " + schema.getName());
            }

            sequence.setDepType(type);

            PgTable table = schema.getTable(tableName);

            if (table == null) {
                throw new IllegalStateException("Table " + tableName + " not found in schema " + schema.getName());
            }

            PgColumn column = table.getColumn(columnName);

            if (column == null) {
                throw new IllegalStateException("Column " + columnName + " not found in table " + tableName + " in schema " + schema.getName());
            }

            column.setSequence(sequence);

            sequence.setOwner(column);

            log.debug("Found sequence link: {} = {}", sequenceName, tableName);
        });
    }

    private void readIndexes(PgSchema schema) {
        String query = getQuery("readIndexes.sql");

        sqlClient.executeQuery(MessageFormat.format(query, schema.getName(), StringUtils.join(PgConstraintType.names(), "\',\'")), (ResultSet rs) -> {
            String tableName = rs.getString("tablename").toLowerCase();
            String indexName = rs.getString("indexname").toLowerCase();
            String indexDefinition = rs.getString("indexdef").trim();

            indexDefinition = indexDefinition.replace(" " + schema.getName() + ".", " ");
            indexDefinition = indexDefinition.replace(" \"" + schema.getName() + "\".", " ");

            PgTable table = schema.getTable(tableName);
            if (table == null) {
                log.warn("Ignore index {}, related table or materialized view not found", indexName);
                return;
            }

            PgIndex index = table.addIndex(indexName, indexDefinition);

            log.debug("Found index: {}", index.getName());
        });
    }

    private void readViews(PgSchema schema) {
        String query = getQuery("readViews.sql");

        sqlClient.executeQuery(String.format(query, schema.getName()), (ResultSet rs) -> {
            String viewName = rs.getString("viewname").toLowerCase();
            String viewDefinition = rs.getString("definition").trim();

            viewDefinition = viewDefinition.replace(" " + schema.getName() + ".", " ");
            viewDefinition = viewDefinition.replace(" \"" + schema.getName() + "\".", " ");

            PgView view = new PgView(viewName, viewDefinition);

            schema.addView(view);

            log.debug("Found view: {}", view.getName());
        });
    }

    private void readConstraints(PgSchema schema) {
        String query = getQuery("readConstraints.sql");

        Map<String, PgTable> tablesByName = schema.getTablesByName();

        sqlClient.executeQuery(MessageFormat.format(query, schema.getName(), StringUtils.join(PgConstraintType.names(), "\',\'")), (ResultSet rs) -> {
            PgTable pgTable = tablesByName.get(rs.getString("table_name"));
            String constraintType = rs.getString("constraint_type");
            String constraintName = rs.getString("constraint_name");
            String columnName = rs.getString("column_name");

            PgColumn column = pgTable.getColumn(columnName);
            if (column == null) {
                throw new IllegalStateException("Read constraint " + constraintName + "  failed: Column " + columnName + " not found in table " + pgTable.getName() + " in schema " + schema.getName());
            }

            if (PgConstraintType.UNIQUE.getName().equals(constraintType)) {
                pgTable.addUniqueConstraint(constraintName, column);
                log.debug("Found unique constraint: {}", constraintName);
            } else if (PgConstraintType.PRIMARY.getName().equals(constraintType)) {
                pgTable.addPrimaryKey(constraintName, column);
                log.debug("Found primary constraint: {}", constraintName);
            }
        });
    }

    private void readForeignKeys(PgSchema schema) {
        String query = getQuery("readForeignKeys.sql");

        sqlClient.executeQuery(MessageFormat.format(query, schema.getName()), (ResultSet rs) -> {
            String constraintName = rs.getString("constraint_name");
            String tableName = rs.getString("table_name").toLowerCase();
            String columnName = rs.getString("column_name").toLowerCase();
            String foreignTableName = rs.getString("foreign_table_name").toLowerCase();
            String foreignColumnName = rs.getString("foreign_column_name").toLowerCase();

            PgTable table = schema.getTable(tableName);
            if (table == null) {
                throw new IllegalStateException(String.format("Read constraint %s ON %s(%s) failed: Table %s not found in schema %s",
                        constraintName, foreignTableName, foreignColumnName, tableName, schema.getName()));
            }

            PgTable referenceTable = schema.getTable(foreignTableName);
            if (referenceTable == null) {
                throw new IllegalStateException(String.format("Read constraint %s ON %s(%s) failed: Table %s not found in schema %s",
                        constraintName, foreignTableName, foreignColumnName, tableName, schema.getName()));
            }

            PgColumn referenceColumn = referenceTable.getColumn(foreignColumnName);
            if (referenceColumn == null) {
                throw new IllegalStateException(String.format("Read constraint %s ON %s(%s) failed: Column %s not found in table %s in schema %s",
                        constraintName, foreignTableName, foreignColumnName, foreignColumnName, foreignTableName, schema.getName()));
            }

            table.addForeignKey(constraintName, columnName, referenceColumn);
        });
    }

}
