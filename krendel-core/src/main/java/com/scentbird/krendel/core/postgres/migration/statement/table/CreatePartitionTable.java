package com.scentbird.krendel.core.postgres.migration.statement.table;

import com.scentbird.krendel.core.postgres.migration.statement.Statement;
import com.scentbird.krendel.core.postgres.model.PgTable;

/**
 * @author Efim Matytsin
 */
public class CreatePartitionTable implements Statement {
    private final PgTable pgTable;

    public CreatePartitionTable(PgTable pgTable) {
        this.pgTable = pgTable;
    }

    @Override
    public String getQuery() {
        return "CREATE TABLE \"" + pgTable.getName() + "\" PARTITION OF \"" + pgTable.getParent().getName() +
                "\"\n    " + pgTable.getPartitionBound() + ";";
    }
}
