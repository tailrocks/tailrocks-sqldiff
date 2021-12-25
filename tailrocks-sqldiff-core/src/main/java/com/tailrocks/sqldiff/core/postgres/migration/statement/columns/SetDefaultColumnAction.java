package com.tailrocks.sqldiff.core.postgres.migration.statement.columns;

import com.tailrocks.sqldiff.core.postgres.model.PgColumn;

public class SetDefaultColumnAction implements AlterColumnAction {
    private final PgColumn column;

    public SetDefaultColumnAction(PgColumn column) {
        this.column = column;
    }

    @Override
    public String getQuery() {
        return "SET DEFAULT " + column.getDefaultValue();
    }
}
