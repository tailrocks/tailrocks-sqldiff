package com.tailrocks.sqldiff.core.postgres.migration.statement.columns;

public class DropDefaultColumnAction implements AlterColumnAction {
    @Override
    public String getQuery() {
        return "DROP DEFAULT";
    }
}
