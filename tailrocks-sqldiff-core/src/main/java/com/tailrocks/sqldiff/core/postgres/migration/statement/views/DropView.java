package com.scentbird.krendel.core.postgres.migration.statement.view;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.scentbird.krendel.core.postgres.model.PgView;

/**
 * @author Efim Matytsin
 */
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
