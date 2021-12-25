package com.scentbird.krendel.core.postgres.migration.statement.table;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.scentbird.krendel.core.postgres.model.PgTable;

/**
 * @author Efim Matytsin
 */
public class CommentOnTable implements Statement {
    private final PgTable table;

    public CommentOnTable(PgTable table) {
        this.table = table;
    }

    @Override
    public String getQuery() {
        String comment = table.getDescription() != null ? "'" + table.getDescription() + "'" : "NULL";
        return  "COMMENT ON TABLE \"" + table.getName() + "\" IS " + comment + ";";
    }
}
