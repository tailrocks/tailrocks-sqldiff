package com.tailrocks.sqldiff.core.postgres.migration.statement.columns;

public class SetNotNullColumnAction implements AlterColumnAction {
    @Override
    public String getQuery() {
        return "SET NOT NULL";
    }
}
