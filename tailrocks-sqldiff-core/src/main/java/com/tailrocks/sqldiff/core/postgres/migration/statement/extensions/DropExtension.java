package com.tailrocks.sqldiff.core.postgres.migration.statement.extensions;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgExtension;

public class DropExtension implements Statement {
    private final PgExtension pgExtension;

    public DropExtension(PgExtension pgExtension) {
        this.pgExtension = pgExtension;
    }

    @Override
    public String getQuery() {
        return "DROP EXTENSION \"" + pgExtension.getName() + "\";";
    }
}
