package com.tailrocks.sqldiff.core.postgres.migration.statement.columns;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgColumn;

public class CommentOnColumn implements Statement {
    private final PgColumn column;

    public CommentOnColumn(PgColumn column) {
        this.column = column;
    }

    @Override
    public String getQuery() {
        String comment = column.getDescription() != null ? "'" + column.getDescription() + "'" : "NULL";
        return "COMMENT ON COLUMN \"" + column.getTable().getName() + "\".\"" + column.getName() + "\" IS " + comment + ";";
    }
}
