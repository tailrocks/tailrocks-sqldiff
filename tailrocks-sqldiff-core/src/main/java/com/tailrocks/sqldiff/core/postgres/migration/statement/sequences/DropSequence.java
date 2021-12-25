package com.scentbird.krendel.core.postgres.migration.statement.sequence;

import com.tailrocks.sqldiff.core.postgres.migration.MigrationOptions;
import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.scentbird.krendel.core.postgres.model.PgSequence;

/**
 * @author Efim Matytsin
 */
public class DropSequence implements Statement {
    private final PgSequence pgSequence;
    private final MigrationOptions migrationOptions;

    public DropSequence(PgSequence pgSequence, MigrationOptions migrationOptions) {
        this.pgSequence = pgSequence;
        this.migrationOptions = migrationOptions;
    }

    @Override
    public String getQuery() {
        String attrs = migrationOptions.isDropSequenceIfExists() ? "IF EXISTS " : "";
        return "DROP SEQUENCE " + attrs + "\"" + pgSequence.getName() + "\";";
    }
}
