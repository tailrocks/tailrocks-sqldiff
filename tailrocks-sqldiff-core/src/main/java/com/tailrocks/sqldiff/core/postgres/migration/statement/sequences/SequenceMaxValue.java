package com.scentbird.krendel.core.postgres.migration.statement.sequence;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;

/**
 * @author Efim Matytsin
 */
public class SequenceMaxValue implements Statement {
    private final long maxValue;

    public SequenceMaxValue(long maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public String getQuery() {
        if (maxValue == 9223372036854775807L) {
            return "NO MAXVALUE";
        }
        return "MAXVALUE " + maxValue;
    }
}
