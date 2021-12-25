package com.scentbird.krendel.core.postgres.migration.statement.alter.column;

import com.scentbird.krendel.core.postgres.model.PgColumn;

/**
 * @author Efim Matytsin
 */
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
