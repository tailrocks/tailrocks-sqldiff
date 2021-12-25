package com.scentbird.krendel.core.postgres.migration.statement.alter.table;

/**
 * @author Efim Matytsin
 */
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
