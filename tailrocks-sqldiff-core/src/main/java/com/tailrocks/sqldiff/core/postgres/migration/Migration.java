package com.tailrocks.sqldiff.core.postgres.migration;

import com.tailrocks.sqldiff.core.postgres.Postgres12TableLockLevel;
import com.tailrocks.sqldiff.core.postgres.diff.Diff;
import com.tailrocks.sqldiff.core.postgres.diff.DiffItem;
import com.tailrocks.sqldiff.core.postgres.diff.DiffOperation;
import com.tailrocks.sqldiff.core.postgres.model.PgColumn;
import com.tailrocks.sqldiff.core.postgres.model.PgEnum;
import com.tailrocks.sqldiff.core.postgres.model.PgExtension;
import com.tailrocks.sqldiff.core.postgres.model.PgForeignKey;
import com.tailrocks.sqldiff.core.postgres.model.PgIndex;
import com.tailrocks.sqldiff.core.postgres.model.PgPrimaryKey;
import com.tailrocks.sqldiff.core.postgres.model.PgSequence;
import com.tailrocks.sqldiff.core.postgres.model.PgTable;
import com.tailrocks.sqldiff.core.postgres.model.PgUniqueConstraint;
import com.tailrocks.sqldiff.core.postgres.model.PgView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.tailrocks.sqldiff.core.postgres.Postgres12TableLockLevel.*;
import static java.util.Collections.*;

public class Migration {

    private static final Logger log = LoggerFactory.getLogger(Migration.class.getSimpleName());

    private Diff diff;
    private MigrationOptions options;
    private MigrationReport report;

    public Migration(Diff diff) {
        this(diff, null);
    }

    public Migration(Diff diff, MigrationOptions options) {
        this.diff = diff;
        this.options = options != null ? options : new MigrationOptions();
    }

    public MigrationReport generate() {
        report = new MigrationReport();

        for (DiffItem diffItem : diff.getChanges()) {
            switch (diffItem.getOperation()) {
                case INSERT:
                    if (diffItem.getAfter() instanceof PgExtension) {
                        report.addMigration(createExtension((PgExtension) diffItem.getAfter()));
                    }
                    if (diffItem.getAfter() instanceof PgEnum) {
                        report.addMigration(createEnum((PgEnum) diffItem.getAfter()));
                    }
                    if (diffItem.getAfter() instanceof PgSequence) {
                        report.addMigration(createSequence((PgSequence) diffItem.getAfter()));
                    }
                    if (diffItem.getAfter() instanceof PgTable) {
                        report.addMigrations(createTable((PgTable) diffItem.getAfter()));
                    }
                    if (diffItem.getAfter() instanceof PgColumn) {
                        report.addMigrations(createColumn((PgColumn) diffItem.getAfter()));
                    }
                    if (diffItem.getAfter() instanceof PgIndex) {
                        report.addMigration(createIndex((PgIndex) diffItem.getAfter()));
                    }
                    if (diffItem.getAfter() instanceof PgView) {
                        report.addMigration(createView((PgView) diffItem.getAfter()));
                    }
                    if (diffItem.getAfter() instanceof PgPrimaryKey) {
                        report.addMigration(createPrimaryKey((PgPrimaryKey) diffItem.getAfter()));
                    }
                    if (diffItem.getAfter() instanceof PgUniqueConstraint) {
                        report.addMigration(createUniqueConstraint((PgUniqueConstraint) diffItem.getAfter()));
                    }
                    if (diffItem.getAfter() instanceof PgForeignKey) {
                        report.addMigrations(createForeignKey((PgForeignKey) diffItem.getAfter()));
                    }
                    break;
                case REMOVE:
                    if (diffItem.getBefore() instanceof PgExtension) {
                        report.addMigration(dropExtension((PgExtension) diffItem.getBefore()));
                    }
                    if (diffItem.getBefore() instanceof PgEnum) {
                        report.addMigration(dropEnum((PgEnum) diffItem.getBefore()));
                    }
                    if (diffItem.getBefore() instanceof PgTable) {
                        report.addMigration(dropTable((PgTable) diffItem.getBefore()));
                    }
                    if (diffItem.getBefore() instanceof PgColumn) {
                        report.addMigration(dropColumn((PgColumn) diffItem.getBefore()));
                    }
                    if (diffItem.getBefore() instanceof PgIndex) {
                        report.addMigration(dropIndex((PgIndex) diffItem.getBefore()));
                    }
                    if (diffItem.getBefore() instanceof PgView) {
                        report.addMigration(dropView((PgView) diffItem.getBefore()));
                    }
                    if (diffItem.getBefore() instanceof PgPrimaryKey) {
                        report.addMigration(dropPrimaryKey((PgPrimaryKey) diffItem.getBefore()));
                    }
                    if (diffItem.getBefore() instanceof PgUniqueConstraint) {
                        report.addMigration(dropUniqueConstraint((PgUniqueConstraint) diffItem.getBefore()));
                    }
                    if (diffItem.getBefore() instanceof PgForeignKey) {
                        report.addMigration(dropForeignKey((PgForeignKey) diffItem.getBefore()));
                    }
                    if (diffItem.getBefore() instanceof PgSequence) {
                        report.addMigration(dropSequence((PgSequence) diffItem.getBefore()));
                    }
                    break;
                case CHANGE:
                    if (diffItem.getBefore() instanceof PgEnum &&
                            diffItem.getAfter() instanceof PgEnum) {
                        report.addMigrations(compareEnum((PgEnum) diffItem.getBefore(), (PgEnum) diffItem.getAfter()));
                    }
                    if (diffItem.getBefore() instanceof PgColumn &&
                            diffItem.getAfter() instanceof PgColumn) {
                        report.addMigrations(compareColumn((PgColumn) diffItem.getBefore(), (PgColumn) diffItem.getAfter()));
                    }
                    if (diffItem.getBefore() instanceof PgTable &&
                            diffItem.getAfter() instanceof PgTable) {
                        report.addMigrations(compareTable((PgTable) diffItem.getBefore(), (PgTable) diffItem.getAfter()));
                    }
                    if (diffItem.getBefore() instanceof PgIndex &&
                            diffItem.getAfter() instanceof PgIndex) {
                        report.addMigrations(compareIndexes((PgIndex) diffItem.getBefore(), (PgIndex) diffItem.getAfter()));
                    }
                    if (diffItem.getBefore() instanceof PgView &&
                            diffItem.getAfter() instanceof PgView) {
                        report.addMigrations(compareViews((PgView) diffItem.getBefore(), (PgView) diffItem.getAfter()));
                    }
                    if (diffItem.getBefore() instanceof PgPrimaryKey &&
                            diffItem.getAfter() instanceof PgPrimaryKey) {
                        report.addMigrations(comparePrimaryKeys((PgPrimaryKey) diffItem.getBefore(), (PgPrimaryKey) diffItem.getAfter()));
                    }
                    if (diffItem.getBefore() instanceof PgUniqueConstraint &&
                            diffItem.getAfter() instanceof PgUniqueConstraint) {
                        report.addMigrations(compareUniqueIndexes((PgUniqueConstraint) diffItem.getBefore(), (PgUniqueConstraint) diffItem.getAfter()));
                    }
                    if (diffItem.getBefore() instanceof PgForeignKey &&
                            diffItem.getAfter() instanceof PgForeignKey) {
                        report.addMigrations(compareForeignKeys((PgForeignKey) diffItem.getBefore(), (PgForeignKey) diffItem.getAfter()));
                    }
                    break;
            }
        }

        return report;
    }

    private MigrationItem createExtension(PgExtension item) {
        String query = "CREATE EXTENSION \"" + item.getName() + "\";";

        return new MigrationItem(DiffOperation.INSERT, query, emptyMap());
    }

    private MigrationItem createEnum(PgEnum item) {
        String values = item.getValues().stream()
                .map(it -> "'" + it + "'")
                .collect(Collectors.joining(", "));

        String query = "CREATE TYPE \"" + item.getName() + "\" AS ENUM (" + values + ");";

        return new MigrationItem(DiffOperation.INSERT, query, emptyMap());
    }

    private MigrationItem createSequence(PgSequence item) {
        String stringBuilder = "CREATE SEQUENCE " + item.getName() + "\n" +
                "START WITH " + item.getStartValue() + "\n" +
                "INCREMENT BY " + item.getIncrementBy() + "\n" +
                getSequenceMinValue(item.getMinValue()) + "\n" +
                getSequenceMaxValue(item.getMaxValue()) + "\n" +
                "CACHE " + item.getCacheSize() + ";";

        return new MigrationItem(DiffOperation.INSERT, stringBuilder, emptyMap());
    }

    private String getSequenceMinValue(long minValue) {
        if (minValue == 1L) {
            return "NO MINVALUE";
        }
        return "MINVALUE " + minValue;
    }

    private String getSequenceMaxValue(long maxValue) {
        if (maxValue == 9223372036854775807L) {
            return "NO MAXVALUE";
        }
        return "MAXVALUE " + maxValue;
    }

    private MigrationItem dropSequence(PgSequence item) {
        if (options.isIgnoreHint()) {
            report.getIgnoreSequences().add(item.getName());
        }

        String attrs = options.isDropSequenceIfExists() ? "IF EXISTS " : "";
        String query = "DROP SEQUENCE " + attrs + "\"" + item.getName() + "\";";
        return new MigrationItem(DiffOperation.REMOVE, query, emptyMap());
    }

    private MigrationItem dropExtension(PgExtension item) {
        if (options.isIgnoreHint()) {
            report.getIgnoreExtensions().add(item.getName());
        }

        String query = "DROP EXTENSION \"" + item.getName() + "\";";
        return new MigrationItem(DiffOperation.REMOVE, query, emptyMap());
    }

    private MigrationItem dropEnum(PgEnum item) {
        if (options.isIgnoreHint()) {
            report.getIgnoreEnums().add(item.getName());
        }

        String query = "DROP TYPE \"" + item.getName() + "\";";
        return new MigrationItem(DiffOperation.REMOVE, query, emptyMap());
    }

    private List<MigrationItem> compareEnum(PgEnum before, PgEnum after) {
        List<String> beforeValues = new ArrayList<>(before.getValues());
        List<String> afterValues = new ArrayList<>(after.getValues());

        beforeValues.removeAll(after.getValues());
        afterValues.removeAll(before.getValues());

        if (beforeValues.isEmpty() && !afterValues.isEmpty()) {
            List<MigrationItem> result = new ArrayList<>();

            for (String value : afterValues) {
                String query = "ALTER TYPE \"" + after.getName() + "\" ADD VALUE '" + value + "';";

                result.add(new MigrationItem(DiffOperation.CHANGE, query, emptyMap()));
            }

            return result;
        } else {
            // TODO delete value from enum
            throw new RuntimeException("Not supported yet");
        }
    }

    private List<MigrationItem> createTable(PgTable table) {
        List<MigrationItem> result = new ArrayList<>();

        if (table.isPartition()) {
            result.add(createPartitionTable(table));
        } else {
            result.add(createMainTable(table));
        }

        if (table.getDescription() != null) {
            result.add(createCommentOnTable(table));
        }

        for (PgColumn column : table.getColumns()) {
            if (column.getSequence() != null) {
                result.addAll(attachSequenceOrIdentity(column));
            }
            if (column.getDescription() != null) {
                result.add(createCommentOnColumn(column));
            }
        }

        return result;
    }

    private MigrationItem createMainTable(PgTable table) {
        List<String> columns = new ArrayList<>();

        for (PgColumn column : table.getColumns()) {
            if (diff.getOptions().isIgnoreColumn(table.getName(), column.getName())) {
                log.debug("Ignored column during creation: {}.{}", table.getName(), column.getName());
            } else {
                // column name
                String columnLine = column.getName() + " ";

                // column type
                columnLine += getColumnType(column);

                // default value
                if (column.getDefaultValue() != null) {
                    columnLine += " DEFAULT " + column.getDefaultValue();
                }

                if (!column.isNullable()) {
                    columnLine += " NOT NULL";
                }

                columns.add(columnLine);
            }
        }

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("CREATE TABLE ");
        stringBuilder.append("\"");
        stringBuilder.append(table.getName());
        stringBuilder.append("\"");
        stringBuilder.append(" (\n");

        String columnsData = columns.stream()
                // add indentation
                .map(it -> "    " + it)
                .collect(Collectors.joining(",\n"));

        stringBuilder.append(columnsData);
        stringBuilder.append("\n");
        stringBuilder.append(")");

        if (table.getPartitionKeyDefinition() != null) {
            stringBuilder.append("\nPARTITION BY ");
            stringBuilder.append(table.getPartitionKeyDefinition());
        }

        stringBuilder.append(";");

        return new MigrationItem(DiffOperation.INSERT, stringBuilder.toString(), emptyMap());
    }

    private MigrationItem createPartitionTable(PgTable table) {
        String query = "CREATE TABLE \"" + table.getName() + "\" PARTITION OF \"" + table.getParent().getName() +
                "\"\n    " + table.getPartitionBound() + ";";

        return new MigrationItem(DiffOperation.INSERT, query, emptyMap());
    }

    private MigrationItem dropTable(PgTable table) {
        if (options.isIgnoreHint()) {
            report.getIgnoreTables().add(table.getName());
        }

        String attrs = options.isDropTableIfExists() ? "IF EXISTS " : "";
        String query = "DROP TABLE " + attrs + "\"" + table.getName() + "\";";
        return new MigrationItem(DiffOperation.REMOVE, query, singletonMap(table, ACCESS_EXCLUSIVE));
    }

    private List<MigrationItem> createColumn(PgColumn column) {
        String attrs = options.isAddColumnIfNotExists() ? "IF NOT EXISTS " : "";
        String query = "ALTER TABLE \"" + column.getTable().getName() + "\"\n    ADD COLUMN " + attrs + "\"" +
                column.getName() + "\" ";

        query += getColumnType(column);

        // default value
        if (!options.isSafeAddDefaultColumn() && column.getDefaultValue() != null) {
            query += " DEFAULT " + column.getDefaultValue();
        }

        if (!options.isSafeAddNotNullColumn() && !column.isNullable()) {
            query += " NOT NULL";
        }

        query += ";";

        List<MigrationItem> result = new ArrayList<>();

        //TODO check level
        result.add(new MigrationItem(DiffOperation.INSERT, query, singletonMap(column.getTable(), ACCESS_EXCLUSIVE)));

        if (options.isSafeAddDefaultColumn() && column.getDefaultValue() != null) {
            query = "ALTER TABLE \"" + column.getTable().getName() + "\"\n    ALTER COLUMN \"" + column.getName() +
                    "\" SET DEFAULT " + column.getDefaultValue() + ";";
            //TODO check level
            result.add(new MigrationItem(DiffOperation.CHANGE, query, singletonMap(column.getTable(), ACCESS_EXCLUSIVE)));

            if (options.isSafeAddNotNullColumn() && !column.isNullable()) {
                query = "UPDATE \"" + column.getTable().getName() + "\"\nSET \"" + column.getName() +
                        "\" = DEFAULT\nWHERE \"" + column.getName() + "\" IS NULL;";
                result.add(new MigrationItem(DiffOperation.CHANGE, query, singletonMap(column.getTable(), ROW_EXCLUSIVE)));
            }
        }

        if (options.isSafeAddNotNullColumn() && !column.isNullable()) {
            query = "ALTER TABLE \"" + column.getTable().getName() + "\"\n    ALTER COLUMN \"" + column.getName() +
                    "\" SET NOT NULL;";
            //TODO check level
            result.add(new MigrationItem(DiffOperation.CHANGE, query, singletonMap(column.getTable(), ACCESS_EXCLUSIVE)));
        }

        if (column.getDescription() != null) {
            result.add(createCommentOnColumn(column));
        }

        return result;
    }

    private MigrationItem dropColumn(PgColumn column) {
        if (options.isIgnoreHint()) {
            report.getIgnoreColumns().putIfAbsent(column.getTable().getName(), new LinkedHashSet<>());
            report.getIgnoreColumns().get(column.getTable().getName()).add(column.getName());
        }

        String attrs = options.isDropColumnIfExists() ? "IF EXISTS " : "";
        String query = "ALTER TABLE \"" + column.getTable().getName() + "\"\n    DROP COLUMN " + attrs + "\"" +
                column.getName() + "\";";
        //TODO check
        return new MigrationItem(DiffOperation.REMOVE, query, singletonMap(column.getTable(), ACCESS_EXCLUSIVE));
    }

    private String getColumnType(PgColumn column) {
        // column type
        if (column.getColumnType().getUdtName().equalsIgnoreCase("varchar")) {
            return column.getColumnType().getDataType() + "(" + column.getColumnType().getCharacterMaximumLength() + ")";
        } else if (column.getColumnType().getDataType().equalsIgnoreCase("USER-DEFINED")) {
            return column.getColumnType().getUdtName();
        } else if (column.getColumnType().getDataType().equalsIgnoreCase("ARRAY")) {
            return column.getColumnType().getUdtName();
        } else if (column.getColumnType().getDataType().equalsIgnoreCase("numeric")) {
            return column.getColumnType().getDataType() + "(" + column.getColumnType().getNumericPrecision() + "," +
                    column.getColumnType().getNumericScale() + ")";
        } else {
            return column.getColumnType().getDataType();
        }
    }

    private List<MigrationItem> compareColumn(PgColumn before, PgColumn after) {
        List<MigrationItem> result = new ArrayList<>();

        // compare default value
        if (!Objects.equals(before.getDefaultValue(), after.getDefaultValue())) {
            if (options.isIgnoreHint()) {
                report.getIgnoreColumnsDefaultValue().putIfAbsent(after.getTable().getName(), new LinkedHashSet<>());
                report.getIgnoreColumnsDefaultValue().get(after.getTable().getName()).add(after.getName());
            }

            if (before.getDefaultValue() == null) {
                String query = "ALTER TABLE \"" + after.getTable().getName() + "\"\n    ALTER COLUMN \"" + after.getName() + "\" SET DEFAULT " + after.getDefaultValue() + ";";
                result.add(new MigrationItem(DiffOperation.CHANGE, query, singletonMap(after.getTable(), ACCESS_EXCLUSIVE)));
            }
            if (after.getDefaultValue() == null) {
                String query = "ALTER TABLE \"" + after.getTable().getName() + "\"\n    ALTER COLUMN \"" + after.getName() + "\" DROP DEFAULT;";
                result.add(new MigrationItem(DiffOperation.CHANGE, query, singletonMap(after.getTable(), ACCESS_EXCLUSIVE)));
            }
        }

        // compare nullable
        if (!Objects.equals(before.isNullable(), after.isNullable())) {
            if (before.isNullable()) {
                String query = "ALTER TABLE \"" + after.getTable().getName() + "\"\n    ALTER COLUMN \"" + after.getName() + "\" SET NOT NULL;";
                result.add(new MigrationItem(DiffOperation.CHANGE, query, singletonMap(after.getTable(), ACCESS_EXCLUSIVE)));
            }
            if (after.isNullable()) {
                String query = "ALTER TABLE \"" + after.getTable().getName() + "\"\n    ALTER COLUMN \"" + after.getName() + "\" DROP NOT NULL;";
                result.add(new MigrationItem(DiffOperation.CHANGE, query, singletonMap(after.getTable(), ACCESS_EXCLUSIVE)));
            }
        }

        // compare sequences
        if (!Objects.equals(before.getSequence(), after.getSequence())) {
            if (before.getSequence() == null) {
                result.addAll(attachSequenceOrIdentity(after));
                result.add(setNextValForSequence(after));
            } else if (after.getSequence() == null) {
                result.add(detachSequence(before.getSequence()));
            } else {
                // switch to use identity
                if (after.getSequence().getDepType() != null &&
                        after.getSequence().getDepType().equalsIgnoreCase("i") &&
                        (
                                before.getSequence().getDepType() == null ||
                                        (
                                                before.getSequence().getDepType() != null &&
                                                        before.getSequence().getDepType().equalsIgnoreCase("a")
                                        )
                        )
                ) {
                    result.add(detachSequence(before.getSequence()));
                    result.add(dropSequence(before.getSequence()));
                    result.addAll(attachSequenceOrIdentity(after));
                    result.add(setNextValForSequence(after));
                }

                // attach sequence
                else if (before.getSequence().getDepType() == null &&
                        after.getSequence().getDepType() != null &&
                        Objects.equals(before.getSequence().getName(), after.getSequence().getName()) &&
                        after.getSequence().getDepType().equalsIgnoreCase("a")) {
                    result.addAll(attachSequenceOrIdentity(after));
                    result.add(setNextValForSequence(after));
                }
            }
        }

        // compare types
        if (!Objects.equals(before.getColumnType(), after.getColumnType())) {
            String originalColumnType = getColumnType(before);
            String columnType = getColumnType(after);

            String comment = "-- Current Type: " + originalColumnType;

            String query = "ALTER TABLE \"" + after.getTable().getName() + "\"\n" +
                    "    ALTER COLUMN \"" + after.getName() + "\" SET DATA TYPE " + columnType + ";";
            result.add(new MigrationItem(DiffOperation.CHANGE, query, comment, singletonMap(after.getTable(), ACCESS_EXCLUSIVE)));
        }

        // compare description
        if (!Objects.equals(before.getDescription(), after.getDescription())) {
            result.add(createCommentOnColumn(after));
        }

        return result;
    }

    private MigrationItem createCommentOnColumn(PgColumn column) {
        String comment = column.getDescription() != null ? "'" + escapeSql(column.getDescription()) + "'" : "NULL";
        String query = "COMMENT ON COLUMN \"" + column.getTable().getName() + "\".\"" + column.getName() + "\" IS " + comment + ";";
        return new MigrationItem(DiffOperation.CHANGE, query, emptyMap());
    }

    private List<MigrationItem> attachSequenceOrIdentity(@NotNull PgColumn column) {
        if (column.getSequence().getDepType() != null) {
            if (column.getSequence().getDepType().equalsIgnoreCase("i")) {
                String query = "ALTER TABLE \"" + column.getTable().getName() + "\" ALTER COLUMN \"" +
                        column.getName() + "\" ADD GENERATED BY DEFAULT AS IDENTITY (\n" +
                        "    SEQUENCE NAME \"" + column.getSequence().getName() + "\"\n" +
                        "    START WITH " + column.getSequence().getStartValue() + "\n" +
                        "    INCREMENT BY " + column.getSequence().getIncrementBy() + "\n" +
                        "    " + getSequenceMinValue(column.getSequence().getMinValue()) + "\n" +
                        "    " + getSequenceMaxValue(column.getSequence().getMaxValue()) + "\n" +
                        "    CACHE " + column.getSequence().getCacheSize() + "\n" +
                        ");";

                return Arrays.asList(new MigrationItem(DiffOperation.INSERT, query, singletonMap(column.getTable(), ACCESS_EXCLUSIVE)));
            } else {
                String query = "ALTER SEQUENCE \"" + column.getSequence().getName() + "\" OWNED BY \"" +
                        column.getTable().getName() + "\".\"" + column.getName() + "\";";

                return Arrays.asList(new MigrationItem(DiffOperation.CHANGE, query, emptyMap()));
            }
        } else {
            return new ArrayList<>();
        }
    }

    private MigrationItem detachSequence(@NotNull PgSequence sequence) {
        String query = "ALTER SEQUENCE \"" + sequence.getName() + "\" OWNED BY NONE;";

        return new MigrationItem(DiffOperation.CHANGE, query, emptyMap());
    }

    private MigrationItem setNextValForSequence(@NotNull PgColumn column) {
        String query = "SELECT setval('" + column.getSequence().getName() + "', (SELECT MAX(\"" + column.getName() +
                "\") FROM \"" + column.getTable().getName() + "\") + 1);";

        return new MigrationItem(DiffOperation.CHANGE, query, singletonMap(column.getTable(), ACCESS_SHARE));
    }

    private List<MigrationItem> compareTable(PgTable before, PgTable after) {
        List<MigrationItem> result = new ArrayList<>();

        if (!Objects.equals(before.getDescription(), after.getDescription())) {
            result.add(createCommentOnTable(after));
        }

        return result;
    }

    private MigrationItem createCommentOnTable(PgTable table) {
        String comment = table.getDescription() != null ? "'" + table.getDescription() + "'" : "NULL";
        String query = "COMMENT ON TABLE \"" + table.getName() + "\" IS " + comment + ";";
        return new MigrationItem(DiffOperation.CHANGE, query, emptyMap());
    }

    private MigrationItem createIndex(PgIndex item) {
        String query = item.getDefinition();

        String originalPrefix = query.contains("CREATE UNIQUE INDEX") ? "CREATE UNIQUE INDEX " : "CREATE INDEX ";
        String createIndexQuery = originalPrefix;

        Postgres12TableLockLevel lockLevel = SHARE;

        if (options.isCreateIndexConcurrently()) {
            lockLevel = SHARE_UPDATE_EXCLUSIVE;
            createIndexQuery += "CONCURRENTLY ";
        }

        if (options.isCreateIndexIfNotExists()) {
            createIndexQuery += "IF NOT EXISTS ";
        }

        query = query.replace(originalPrefix, createIndexQuery);

        if (!query.endsWith(";")) {
            query = query + ";";
        }

        return new MigrationItem(DiffOperation.INSERT, query, singletonMap(item.getTable(), lockLevel));
    }

    private MigrationItem dropIndex(PgIndex item) {
        if (options.isIgnoreHint()) {
            report.getIgnoreIndexes().add(item.getName());
        }

        String attrs = options.isDropIndexConcurrently() ? "CONCURRENTLY " : "";
        attrs += options.isDropIndexIfExists() ? "IF EXISTS " : "";

        String query = "DROP INDEX " + attrs + "\"" + item.getName() + "\";";
        return new MigrationItem(DiffOperation.REMOVE, query, singletonMap(item.getTable(), EXCLUSIVE));
    }

    private List<MigrationItem> compareIndexes(PgIndex before, PgIndex after) {
        return Arrays.asList(
                dropIndex(before),
                createIndex(after)
        );
    }

    private MigrationItem createView(PgView item) {
        String query = "CREATE VIEW \"" + item.getName() + "\" AS\n" + item.getDefinition();

        if (!query.endsWith(";")) {
            query = query + ";";
        }

        return new MigrationItem(DiffOperation.INSERT, query, emptyMap());
    }

    private MigrationItem dropView(PgView item) {
        if (options.isIgnoreHint()) {
            report.getIgnoreViews().add(item.getName());
        }

        // todo IF EXISTS
        String query = "DROP VIEW \"" + item.getName() + "\";";
        return new MigrationItem(DiffOperation.REMOVE, query, emptyMap());
    }

    private List<MigrationItem> comparePrimaryKeys(PgPrimaryKey before, PgPrimaryKey after) {
        return Arrays.asList(
                dropPrimaryKey(before),
                createPrimaryKey(after)
        );
    }

    private MigrationItem createPrimaryKey(PgPrimaryKey primaryKey) {
        String query = "ALTER TABLE \"" + primaryKey.getTable().getName() + "\"\n    ADD CONSTRAINT \"" + primaryKey.getName() + "\" PRIMARY KEY (" +
                StringUtils.join(primaryKey.getColumns().stream().map(pk -> pk.getName()).collect(Collectors.toList()), ", ") + ");";
        return new MigrationItem(DiffOperation.INSERT, query, singletonMap(primaryKey.getTable(), ACCESS_EXCLUSIVE));
    }

    private MigrationItem dropPrimaryKey(PgPrimaryKey primaryKey) {
        if (options.isIgnoreHint()) {
            report.getIgnoreConstraints().add(primaryKey.getName());
        }

        String query = "ALTER TABLE \"" + primaryKey.getTable().getName() + "\"\n    DROP CONSTRAINT \"" + primaryKey.getName() + "\";";
        return new MigrationItem(DiffOperation.REMOVE, query, singletonMap(primaryKey.getTable(), ACCESS_EXCLUSIVE));
    }

    private List<MigrationItem> compareUniqueIndexes(PgUniqueConstraint before, PgUniqueConstraint after) {
        return Arrays.asList(
                dropUniqueConstraint(before),
                createUniqueConstraint(after)
        );
    }

    private MigrationItem createUniqueConstraint(PgUniqueConstraint uniqueConstraint) {
        String query = "ALTER TABLE \"" + uniqueConstraint.getTable().getName() + "\"\n    ADD CONSTRAINT \"" + uniqueConstraint.getName() + "\" UNIQUE (" +
                StringUtils.join(uniqueConstraint.getColumns().stream().map(PgColumn::getName).collect(Collectors.toList()), ", ") + ");";
        return new MigrationItem(DiffOperation.INSERT, query, singletonMap(uniqueConstraint.getTable(), ACCESS_EXCLUSIVE));
    }

    private MigrationItem dropUniqueConstraint(PgUniqueConstraint uniqueConstraint) {
        if (options.isIgnoreHint()) {
            report.getIgnoreConstraints().add(uniqueConstraint.getName());
        }

        String query = "ALTER TABLE \"" + uniqueConstraint.getTable().getName() + "\"\n    DROP CONSTRAINT \"" + uniqueConstraint.getName() + "\";";
        return new MigrationItem(DiffOperation.REMOVE, query, singletonMap(uniqueConstraint.getTable(), ACCESS_EXCLUSIVE));
    }

    private List<MigrationItem> compareViews(PgView before, PgView after) {
        return Arrays.asList(
                dropView(before),
                createView(after)
        );
    }

    private List<MigrationItem> createForeignKey(PgForeignKey item) {
        List<MigrationItem> result = new ArrayList<>();

        String query = "ALTER TABLE \"" + item.getColumn().getTable().getName() + "\"\n" +
                "    ADD CONSTRAINT \"" + item.getName() + "\" FOREIGN KEY (\"" + item.getColumn().getName() + "\") " +
                "REFERENCES \"" + item.getReference().getTable().getName() + "\"(\"" +
                item.getReference().getName() + "\")";

        if (options.isSafeCreateForeignKey()) {
            query += " NOT VALID";
        }

        query += ";";

        Map<PgTable, Postgres12TableLockLevel> locks = new HashMap<>();
        locks.put(item.getColumn().getTable(), SHARE_ROW_EXCLUSIVE);
        locks.put(item.getReference().getTable(), SHARE_ROW_EXCLUSIVE);
        result.add(new MigrationItem(DiffOperation.INSERT, query, locks));

        if (options.isSafeCreateForeignKey()) {
            query = "ALTER TABLE \"" + item.getColumn().getTable().getName() + "\"\n" +
                    "    VALIDATE CONSTRAINT \"" + item.getName() + "\";";
            result.add(new MigrationItem(DiffOperation.CHANGE, query, singletonMap(item.getColumn().getTable(), ACCESS_EXCLUSIVE)));
        }

        return result;
    }

    private MigrationItem dropForeignKey(PgForeignKey item) {
        if (options.isIgnoreHint()) {
            report.getIgnoreConstraints().add(item.getName());
        }

        String query = "ALTER TABLE \"" + item.getColumn().getTable().getName() + "\"\n" +
                "    DROP CONSTRAINT \"" + item.getName() + "\";";
        return new MigrationItem(DiffOperation.REMOVE, query, singletonMap(item.getColumn().getTable(), ACCESS_EXCLUSIVE));
    }

    private List<MigrationItem> compareForeignKeys(PgForeignKey before, PgForeignKey after) {
        List<MigrationItem> result = new ArrayList<>();

        result.add(dropForeignKey(before));
        result.addAll(createForeignKey(after));

        return result;
    }

    private String escapeSql(String value) {
        return value.replace("'", "''");
    }

}
