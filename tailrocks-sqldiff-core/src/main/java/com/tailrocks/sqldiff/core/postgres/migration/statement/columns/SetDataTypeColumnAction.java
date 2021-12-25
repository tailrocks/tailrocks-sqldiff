package com.tailrocks.sqldiff.core.postgres.migration.statement.columns;

import com.tailrocks.sqldiff.core.postgres.model.PgColumn;

import static com.tailrocks.sqldiff.core.postgres.migration.MigrationUtils.getColumnType;

public class SetDataTypeColumnAction implements AlterColumnAction {
    private final PgColumn pgColumn;

    public SetDataTypeColumnAction(PgColumn pgColumn) {
        this.pgColumn = pgColumn;
    }

    @Override
    public String getQuery() {
        String columnType = getColumnType(pgColumn);
        return "SET DATA TYPE " + columnType;
    }
}
