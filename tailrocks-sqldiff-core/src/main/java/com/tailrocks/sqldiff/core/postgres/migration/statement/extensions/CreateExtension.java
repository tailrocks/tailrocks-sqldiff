package com.tailrocks.sqldiff.core.postgres.migration.statement.extensions;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgExtension;

public class CreateExtension implements Statement {

    private final PgExtension pgExtension;

    public CreateExtension(PgExtension pgExtension) {
        this.pgExtension = pgExtension;
    }

    @Override
    public String getQuery() {
        return "CREATE EXTENSION \"" + pgExtension.getName() + "\";";
    }
}
