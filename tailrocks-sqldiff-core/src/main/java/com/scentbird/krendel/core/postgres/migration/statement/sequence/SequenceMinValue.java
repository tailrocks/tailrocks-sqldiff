package com.scentbird.krendel.core.postgres.migration.statement.sequence;

import com.scentbird.krendel.core.postgres.migration.statement.Statement;

/**
 * @author Efim Matytsin
 */
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
