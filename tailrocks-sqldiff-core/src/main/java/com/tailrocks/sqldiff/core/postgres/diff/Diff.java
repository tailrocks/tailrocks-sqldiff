package com.scentbird.krendel.core.postgres.diff;

import com.scentbird.krendel.core.postgres.model.PgSchema;

import java.util.ArrayList;
import java.util.List;

public class Diff {

    private final PgSchema sourceSchema;
    private final PgSchema destinationSchema;
    private final DiffOptions options;

    private final List<DiffItem> changes = new ArrayList<>();

    public Diff(PgSchema sourceSchema, PgSchema destinationSchema, DiffOptions options) {
        if (sourceSchema == null) {
            throw new IllegalArgumentException("`sourceSchema` can not be null");
        }
        if (destinationSchema == null) {
            throw new IllegalArgumentException("`destinationSchema` can not be null");
        }
        if (options == null) {
            throw new IllegalArgumentException("`options` can not be null");
        }

        this.sourceSchema = sourceSchema;
        this.destinationSchema = destinationSchema;
        this.options = options;
    }

    public void addDiffItem(DiffItem diffItem) {
        changes.add(diffItem);
    }

    public PgSchema getSourceSchema() {
        return sourceSchema;
    }

    public PgSchema getDestinationSchema() {
        return destinationSchema;
    }

    public List<DiffItem> getChanges() {
        return changes;
    }

    public DiffOptions getOptions() {
        return options;
    }

}
