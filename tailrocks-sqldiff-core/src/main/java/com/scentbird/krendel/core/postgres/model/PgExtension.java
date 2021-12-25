package com.scentbird.krendel.core.postgres.model;

public class PgExtension implements PgElement {

    private final String name;

    public PgExtension(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
