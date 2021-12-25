package com.scentbird.krendel.core.postgres.model;

public class PgView implements PgElement {

    private String name;

    private String definition;

    public PgView(String name, String definition) {
        this.name = name;
        this.definition = definition;
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
