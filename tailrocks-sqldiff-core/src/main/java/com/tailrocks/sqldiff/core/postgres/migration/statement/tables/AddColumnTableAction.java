package com.tailrocks.sqldiff.core.postgres.migration.statement.tables;

import com.tailrocks.sqldiff.core.postgres.migration.MigrationOptions;
import com.tailrocks.sqldiff.core.postgres.model.PgColumn;

import static com.tailrocks.sqldiff.core.postgres.migration.MigrationUtils.getColumnType;

public class AddColumnTableAction implements AlterTableAction {
    private final PgColumn pgColumn;
    private final MigrationOptions migrationOptions;

    public AddColumnTableAction(PgColumn pgColumn, MigrationOptions migrationOptions) {
        this.pgColumn = pgColumn;
        this.migrationOptions = migrationOptions;
    }

    @Override
    public String getQuery() {
        String attrs = migrationOptions.isAddColumnIfNotExists() ? "IF NOT EXISTS " : "";
        String query = "ADD COLUMN " + attrs + "\"" + pgColumn.getName() + "\" ";

        query += getColumnType(pgColumn);

        // default value
        if (!migrationOptions.isSafeAddDefaultColumn() && pgColumn.getDefaultValue() != null) {
            query += " DEFAULT " + pgColumn.getDefaultValue();
        }

        if (!migrationOptions.isSafeAddNotNullColumn() && !pgColumn.isNullable()) {
            query += " NOT NULL";
        }
        return query;
    }
}
