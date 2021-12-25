package com.tailrocks.sqldiff.core.postgres.migration.statement.tables;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgTable;

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
