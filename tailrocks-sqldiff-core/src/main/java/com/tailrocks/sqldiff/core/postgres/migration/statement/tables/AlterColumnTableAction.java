package com.scentbird.krendel.core.postgres.migration.statement.alter.table;

import com.scentbird.krendel.core.postgres.migration.statement.alter.column.AlterColumnAction;
import com.scentbird.krendel.core.postgres.model.PgColumn;

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
