package com.scentbird.krendel.core.postgres.migration.statement.alter.type;

import com.scentbird.krendel.core.postgres.migration.statement.Statement;

/**
 * @author Efim Matytsin
 */
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
