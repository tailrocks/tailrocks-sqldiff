package com.tailrocks.sqldiff.core.postgres.migration.statement.tables;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgTable;

/**
 * @author Efim Matytsin
 */
public class AlterTable implements Statement {
    private final PgTable pgTable;
    private final AlterTableAction action;

    public AlterTable(PgTable pgTable, AlterTableAction action) {
        this.pgTable = pgTable;
        this.action = action;
    }

    @Override
    public String getQuery() {
        return  "ALTER TABLE \"" + pgTable.getName() + "\"\n " + action.getQuery() + ";";
    }
}
