package com.scentbird.krendel.core.postgres.migration.statement.alter.column;

import com.scentbird.krendel.core.postgres.model.PgColumn;

import static com.scentbird.krendel.core.postgres.migration.MigrationUtils.getColumnType;

/**
 * @author Efim Matytsin
 */
public class SetDataTypeColumnAction implements AlterColumnAction {
    private final PgColumn pgColumn;

    public SetDataTypeColumnAction(PgColumn pgColumn) {
        this.pgColumn = pgColumn;
    }

    @Override
    public String getQuery() {
        String columnType = getColumnType(pgColumn);
        return "SET DATA TYPE " + columnType;
    }
}
