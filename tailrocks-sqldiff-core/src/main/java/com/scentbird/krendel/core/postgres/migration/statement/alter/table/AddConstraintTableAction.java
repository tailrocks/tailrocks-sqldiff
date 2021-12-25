package com.scentbird.krendel.core.postgres.migration.statement.alter.table;

import com.scentbird.krendel.core.postgres.migration.statement.alter.table.constraint.AddConstraintTypeAction;

/**
 * @author Efim Matytsin
 */
public class AddConstraintTableAction implements AlterTableAction {
    private final String key;
    private AddConstraintTypeAction type;

    public AddConstraintTableAction(String key) {
        this.key = key;
    }

    public AddConstraintTableAction(String key, AddConstraintTypeAction type) {
        this.key = key;
        this.type = type;
    }

    @Override
    public String getQuery() {
        String query = "ADD CONSTRAINT \"" + key + "\" ";
        if (type != null) {
            query += type.getQuery();
        }
        return query;
    }
}
