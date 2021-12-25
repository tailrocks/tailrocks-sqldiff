package com.scentbird.krendel.core.postgres.migration.statement.column;

import com.scentbird.krendel.core.postgres.migration.statement.Statement;
import com.scentbird.krendel.core.postgres.model.PgColumn;

/**
 * @author Efim Matytsin
 */
public class CommentOnColumn implements Statement {
    private final PgColumn column;

    public CommentOnColumn(PgColumn column) {
        this.column = column;
    }

    @Override
    public String getQuery() {
        String comment = column.getDescription() != null ? "'" + column.getDescription() + "'" : "NULL";
        return  "COMMENT ON COLUMN \"" + column.getTable().getName() + "\".\"" + column.getName() + "\" IS " + comment + ";";
    }
}
