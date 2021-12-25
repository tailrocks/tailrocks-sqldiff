package com.scentbird.krendel.core.postgres.model;

public class PgUniqueConstraint extends PgConstraint {

    public PgUniqueConstraint(PgTable table, String name) {
        super(table, name);
    }

}
