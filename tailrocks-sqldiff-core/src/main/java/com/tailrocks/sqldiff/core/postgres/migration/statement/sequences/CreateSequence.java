package com.tailrocks.sqldiff.core.postgres.migration.statement.sequences;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgSequence;

/**
 * @author Efim Matytsin
 */
public class CreateSequence implements Statement {

    private final PgSequence pgSequence;
    private final SequenceMaxValue sequenceMaxValue;
    private final SequenceMinValue sequenceMinValue;

    public CreateSequence(PgSequence pgSequence) {
        this.pgSequence = pgSequence;
        this.sequenceMaxValue = new SequenceMaxValue(pgSequence.getMaxValue());
        this.sequenceMinValue = new SequenceMinValue(pgSequence.getMinValue());
    }

    @Override
    public String getQuery() {
        return  "CREATE SEQUENCE " + pgSequence.getName() + "\n" +
                "START WITH " + pgSequence.getStartValue() + "\n" +
                "INCREMENT BY " + pgSequence.getIncrementBy() + "\n" +
                sequenceMinValue.getQuery() + "\n" +
                sequenceMaxValue.getQuery() + "\n" +
                "CACHE " + pgSequence.getCacheSize() + ";";
    }
}
