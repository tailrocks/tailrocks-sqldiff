package com.scentbird.krendel.core.postgres.migration.statement.alter.column;

/**
 * @author Efim Matytsin
 */
public class SetNotNullColumnAction implements AlterColumnAction {
    @Override
    public String getQuery() {
        return "SET NOT NULL";
    }
}
