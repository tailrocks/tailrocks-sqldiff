package com.scentbird.krendel.core.postgres.migration.statement.extension;

import com.scentbird.krendel.core.postgres.migration.statement.Statement;
import com.scentbird.krendel.core.postgres.model.PgExtension;

/**
 * @author Efim Matytsin
 */
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
