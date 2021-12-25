package com.scentbird.krendel.core.postgres.migration.statement.index;

import com.tailrocks.sqldiff.core.postgres.migration.MigrationOptions;
import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.scentbird.krendel.core.postgres.model.PgIndex;

/**
 * @author Efim Matytsin
 */
public class DropIndex implements Statement {
    private final PgIndex pgIndex;
    private final MigrationOptions migrationOptions;

    public DropIndex(PgIndex pgIndex, MigrationOptions migrationOptions) {
        this.pgIndex = pgIndex;
        this.migrationOptions = migrationOptions;
    }

    @Override
    public String getQuery() {
        String attrs = migrationOptions.isDropIndexConcurrently() ? "CONCURRENTLY " : "";
        attrs += migrationOptions.isDropIndexIfExists() ? "IF EXISTS " : "";

        String query = "DROP INDEX " + attrs + "\"" + pgIndex.getName() + "\";";
        return null;
    }
}
