package com.tailrocks.sqldiff.core.postgres.migration.statement.views;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgView;

public class DropView implements Statement {
    private final PgView pgView;

    public DropView(PgView pgView) {
        this.pgView = pgView;
    }

    @Override
    public String getQuery() {
        // todo IF EXISTS
        return "DROP VIEW \"" + pgView.getName() + "\";";
    }
}
