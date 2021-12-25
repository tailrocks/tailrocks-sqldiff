package com.tailrocks.sqldiff.core.postgres.migration.statement.views;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgView;

/**
 * @author Efim Matytsin
 */
public class CreateView implements Statement {
    private final PgView pgView;

    public CreateView(PgView pgView) {
        this.pgView = pgView;
    }

    @Override
    public String getQuery() {
        String query = "CREATE VIEW \"" + pgView.getName() + "\" AS\n" + pgView.getDefinition();

        if (!query.endsWith(";")) {
            query = query + ";";
        }
        return query;
    }
}
