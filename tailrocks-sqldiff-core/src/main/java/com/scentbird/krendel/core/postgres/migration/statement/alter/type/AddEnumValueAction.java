package com.scentbird.krendel.core.postgres.migration.statement.alter.type;

/**
 * @author Efim Matytsin
 */
public class AddEnumValueAction implements AlterTypeAction {
    private final String value;

    public AddEnumValueAction(String value) {
        this.value = value;
    }

    @Override
    public String getQuery() {
        return "ADD VALUE '" + value + "'";
    }
}
