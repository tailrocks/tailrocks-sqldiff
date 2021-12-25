package com.tailrocks.sqldiff.core.postgres.migration.statement.tables;

import com.tailrocks.sqldiff.core.postgres.migration.MigrationOptions;
import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgTable;

public class DropTable implements Statement {
    private final PgTable pgTable;
    private final MigrationOptions migrationOptions;

    public DropTable(PgTable pgTable, MigrationOptions migrationOptions) {
        this.pgTable = pgTable;
        this.migrationOptions = migrationOptions;
    }

    @Override
    public String getQuery() {
        String attrs = migrationOptions.isDropTableIfExists() ? "IF EXISTS " : "";
        return "DROP TABLE " + attrs + "\"" + pgTable.getName() + "\";";
    }
}
