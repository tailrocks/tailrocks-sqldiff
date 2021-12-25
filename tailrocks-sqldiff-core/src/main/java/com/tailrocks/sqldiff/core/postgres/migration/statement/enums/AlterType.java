package com.tailrocks.sqldiff.core.postgres.migration.statement.enums;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;

public class AlterType implements Statement {
    private final String name;
    private final AlterTypeAction action;

    public AlterType(String name, AlterTypeAction action) {
        this.name = name;
        this.action = action;
    }

    @Override
    public String getQuery() {
        return "ALTER TYPE \"" + name + "\" " + action.getQuery() + ";";
    }
}
