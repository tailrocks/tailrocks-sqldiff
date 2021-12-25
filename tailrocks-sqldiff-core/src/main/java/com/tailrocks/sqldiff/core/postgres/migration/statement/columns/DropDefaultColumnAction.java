package com.scentbird.krendel.core.postgres.migration.statement.alter.column;

/**
 * @author Efim Matytsin
 */
public class DropDefaultColumnAction implements AlterColumnAction {
    @Override
    public String getQuery() {
        return "DROP DEFAULT";
    }
}
