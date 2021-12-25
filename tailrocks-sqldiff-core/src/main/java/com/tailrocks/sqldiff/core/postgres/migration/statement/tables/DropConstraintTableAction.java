package com.scentbird.krendel.core.postgres.migration.statement.alter.table;

/**
 * @author Efim Matytsin
 */
public class DropConstraintTableAction implements AlterTableAction {
    private final String key;

    public DropConstraintTableAction(String key) {
        this.key = key;
    }

    @Override
    public String getQuery() {
        return  "DROP CONSTRAINT \"" + key + "\"";
    }
}
