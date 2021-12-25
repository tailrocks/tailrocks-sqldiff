package com.tailrocks.sqldiff.core.postgres.migration.statement.sequences;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;

public class SequenceMinValue implements Statement {
    private final long minValue;

    public SequenceMinValue(long minValue) {
        this.minValue = minValue;
    }

    @Override
    public String getQuery() {
        if (minValue == 1L) {
            return "NO MINVALUE";
        }
        return "MINVALUE " + minValue;
    }
}
