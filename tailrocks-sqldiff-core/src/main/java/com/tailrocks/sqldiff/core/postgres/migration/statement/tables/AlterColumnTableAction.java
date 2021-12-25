package com.tailrocks.sqldiff.core.postgres.migration.statement.tables;

import com.tailrocks.sqldiff.core.postgres.migration.statement.columns.AlterColumnAction;
import com.tailrocks.sqldiff.core.postgres.model.PgColumn;

/**
 * @author Efim Matytsin
 */
public class AlterColumnTableAction implements AlterTableAction {
    private final PgColumn pgColumn;
    private final AlterColumnAction alterColumnSetAction;

    public AlterColumnTableAction(PgColumn pgColumn, AlterColumnAction alterColumnSetAction) {
        this.pgColumn = pgColumn;
        this.alterColumnSetAction = alterColumnSetAction;
    }

    @Override
    public String getQuery() {
        return " ALTER COLUMN \"" + pgColumn.getName() + "\" " + alterColumnSetAction.getQuery();
    }
}
