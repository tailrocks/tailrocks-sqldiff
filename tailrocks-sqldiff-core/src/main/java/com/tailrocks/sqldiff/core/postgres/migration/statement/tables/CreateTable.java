package com.tailrocks.sqldiff.core.postgres.migration.statement.tables;

import com.tailrocks.sqldiff.core.postgres.diff.DiffOptions;
import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgColumn;
import com.tailrocks.sqldiff.core.postgres.model.PgTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.tailrocks.sqldiff.core.postgres.migration.MigrationUtils.getColumnType;

/**
 * @author Efim Matytsin
 */
public class CreateTable implements Statement {
    private static final Logger log = LoggerFactory.getLogger(CreateTable.class.getSimpleName());

    private final PgTable pgTable;
    private final DiffOptions diffOptions;

    public CreateTable(PgTable pgTable, DiffOptions diffOptions) {
        this.pgTable = pgTable;
        this.diffOptions = diffOptions;
    }


    @Override
    public String getQuery() {
        List<String> columns = new ArrayList<>();

        for (PgColumn column : pgTable.getColumns()) {
            if (diffOptions.isIgnoreColumn(pgTable.getName(), column.getName())) {
                log.debug("Ignored column during creation: {}.{}", pgTable.getName(), column.getName());
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
        stringBuilder.append(pgTable.getName());
        stringBuilder.append("\"");
        stringBuilder.append(" (\n");

        String columnsData = columns.stream()
                // add indentation
                .map(it -> "    " + it)
                .collect(Collectors.joining(",\n"));

        stringBuilder.append(columnsData);
        stringBuilder.append("\n");
        stringBuilder.append(")");

        if (pgTable.getPartitionKeyDefinition() != null) {
            stringBuilder.append("\nPARTITION BY ");
            stringBuilder.append(pgTable.getPartitionKeyDefinition());
        }

        stringBuilder.append(";");
        return stringBuilder.toString();
    }
}
