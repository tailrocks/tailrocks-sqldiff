package com.scentbird.krendel.core.postgres.model;

import java.util.ArrayList;
import java.util.List;

public class PgConstraint implements PgElement {

    private PgTable table;

    private String name;
    private List<PgColumn> columns = new ArrayList<>();

    public PgConstraint(PgTable table, String name) {
        this.table = table;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PgColumn> getColumns() {
        return columns;
    }

    public void addColumn(PgColumn column) {
        columns.add(column);
    }

    public PgTable getTable() {
        return table;
    }

    public void setTable(PgTable table) {
        this.table = table;
    }

}
