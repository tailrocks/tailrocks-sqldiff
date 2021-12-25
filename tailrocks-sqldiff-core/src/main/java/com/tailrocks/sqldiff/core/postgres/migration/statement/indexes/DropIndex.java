package com.tailrocks.sqldiff.core.postgres.migration.statement.indexes;

import com.tailrocks.sqldiff.core.postgres.migration.MigrationOptions;
import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgIndex;

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
