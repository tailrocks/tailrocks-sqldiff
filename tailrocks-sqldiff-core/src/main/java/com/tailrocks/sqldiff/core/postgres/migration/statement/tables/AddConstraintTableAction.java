package com.tailrocks.sqldiff.core.postgres.migration.statement.tables;

import com.tailrocks.sqldiff.core.postgres.migration.statement.constraints.AddConstraintTypeAction;

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
