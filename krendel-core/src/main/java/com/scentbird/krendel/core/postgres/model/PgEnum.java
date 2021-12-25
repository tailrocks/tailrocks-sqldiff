package com.scentbird.krendel.core.postgres.model;

import java.util.ArrayList;
import java.util.List;

public class PgEnum implements PgElement {

    // TODO list of columns using this enum

    private int oid;
    private String name;

    private List<String> values = new ArrayList<>();

    public PgEnum(int oid, String name) {
        this.oid = oid;
        this.name = name;
    }

    public int getOid() {
        return oid;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public void addValue(String value) {
        if (!values.contains(value)) {
            values.add(value);
        }
    }

}
