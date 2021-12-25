package com.tailrocks.sqldiff.core.postgres.model;

public class PgIndex implements PgElement {

    private PgTable table;

    private String name;

    private String definition;

    public PgIndex(PgTable table, String name) {
        this.table = table;
        this.name = name;
    }

    public PgTable getTable() {
        return table;
    }

    public void setTable(PgTable table) {
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

}
