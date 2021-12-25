package com.tailrocks.sqldiff.core.postgres.migration.statement.indexes;

import com.tailrocks.sqldiff.core.postgres.migration.MigrationOptions;
import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgIndex;

public class CreateIndex implements Statement {
    private final PgIndex pgIndex;
    private final MigrationOptions migrationOptions;

    public CreateIndex(PgIndex pgIndex, MigrationOptions migrationOptions) {
        this.pgIndex = pgIndex;
        this.migrationOptions = migrationOptions;
    }

    @Override
    public String getQuery() {
        String query = pgIndex.getDefinition();

        String originalPrefix = query.contains("CREATE UNIQUE INDEX") ? "CREATE UNIQUE INDEX " : "CREATE INDEX ";
        String createIndexQuery = originalPrefix;

        if (migrationOptions.isCreateIndexConcurrently()) {
            createIndexQuery += "CONCURRENTLY ";
        }

        if (migrationOptions.isCreateIndexIfNotExists()) {
            createIndexQuery += "IF NOT EXISTS ";
        }

        query = query.replace(originalPrefix, createIndexQuery);

        if (!query.endsWith(";")) {
            query = query + ";";
        }
        return query;
    }
}
