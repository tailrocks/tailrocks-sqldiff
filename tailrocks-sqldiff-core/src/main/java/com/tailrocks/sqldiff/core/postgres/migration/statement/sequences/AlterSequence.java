package com.tailrocks.sqldiff.core.postgres.migration.statement.sequences;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgSequence;

public class AlterSequence implements Statement {
    private final PgSequence pgSequence;
    private final AlterSequenceAction alterSequenceAction;

    public AlterSequence(PgSequence pgSequence, AlterSequenceAction alterSequenceAction) {
        this.pgSequence = pgSequence;
        this.alterSequenceAction = alterSequenceAction;
    }

    @Override
    public String getQuery() {
        return "ALTER SEQUENCE \"" + pgSequence.getName() + "\" " + alterSequenceAction.getQuery() + ";";
    }
}
