package com.tailrocks.sqldiff.core.postgres.migration.statement.tables;

public class ValidateConstraintTableAction implements AlterTableAction {
    private final String key;

    public ValidateConstraintTableAction(String key) {
        this.key = key;
    }

    @Override
    public String getQuery() {
        return "VALIDATE CONSTRAINT \"" + key + "\"";
    }
}
