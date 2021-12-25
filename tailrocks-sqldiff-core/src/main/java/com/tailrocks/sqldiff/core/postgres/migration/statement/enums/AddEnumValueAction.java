package com.tailrocks.sqldiff.core.postgres.migration.statement.enums;

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
