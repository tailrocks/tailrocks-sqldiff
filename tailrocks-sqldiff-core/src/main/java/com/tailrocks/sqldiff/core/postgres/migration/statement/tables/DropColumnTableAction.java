package com.tailrocks.sqldiff.core.postgres.migration.statement.tables;

import com.tailrocks.sqldiff.core.postgres.migration.MigrationOptions;
import com.tailrocks.sqldiff.core.postgres.model.PgColumn;

/**
 * @author Efim Matytsin
 */
public class DropColumnTableAction implements AlterTableAction {
    private final PgColumn pgColumn;
    private final MigrationOptions migrationOptions;

    public DropColumnTableAction(PgColumn pgColumn, MigrationOptions migrationOptions) {
        this.pgColumn = pgColumn;
        this.migrationOptions = migrationOptions;
    }

    @Override
    public String getQuery() {
        String attrs = migrationOptions.isDropColumnIfExists() ? "IF EXISTS " : "";
        return "DROP COLUMN " + attrs + "\"" + pgColumn.getName() + "\"";
    }
}
