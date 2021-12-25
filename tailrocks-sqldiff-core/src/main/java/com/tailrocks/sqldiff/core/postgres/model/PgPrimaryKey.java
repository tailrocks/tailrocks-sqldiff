package com.tailrocks.sqldiff.core.postgres.model;

public class PgPrimaryKey extends PgConstraint {

    public PgPrimaryKey(PgTable table, String name, PgColumn column) {
        super(table, name);
        addColumn(column);
    }

}
