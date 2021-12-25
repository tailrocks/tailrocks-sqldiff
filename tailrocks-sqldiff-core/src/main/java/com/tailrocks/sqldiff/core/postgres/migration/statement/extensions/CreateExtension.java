package com.scentbird.krendel.core.postgres.migration.statement.extension;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.scentbird.krendel.core.postgres.model.PgExtension;

/**
 * @author Efim Matytsin
 */
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
