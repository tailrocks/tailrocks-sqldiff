package com.tailrocks.sqldiff.core.postgres.migration.statement.constraints;

import com.tailrocks.sqldiff.core.postgres.migration.MigrationOptions;
import com.tailrocks.sqldiff.core.postgres.model.PgForeignKey;

/**
 * @author Efim Matytsin
 */
public class ForeignKeyTypeAction implements AddConstraintTypeAction {
    private final PgForeignKey foreignKey;
    private final MigrationOptions migrationOptions;

    public ForeignKeyTypeAction(PgForeignKey foreignKey, MigrationOptions migrationOptions) {
        this.foreignKey = foreignKey;
        this.migrationOptions = migrationOptions;
    }

    @Override
    public String getQuery() {
        String query = "FOREIGN KEY (\"" + foreignKey.getColumn().getName() + "\") " +
                "REFERENCES \"" + foreignKey.getReference().getTable().getName() + "\"(\"" +
                foreignKey.getReference().getName() + "\")";
        if (migrationOptions.isSafeCreateForeignKey()) {
            query += " NOT VALID";
        }
        return query;
    }
}
