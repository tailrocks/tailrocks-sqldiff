package com.tailrocks.sqldiff.core.postgres.migration.statement.columns;

public class DropNotNullColumnAction implements AlterColumnAction {
    @Override
    public String getQuery() {
        return "DROP NOT NULL";
    }
}
