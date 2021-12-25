package com.tailrocks.sqldiff.core.postgres.migration.statement.enums;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.model.PgEnum;

public class DropEnum implements Statement {
    private final PgEnum pgEnum;

    public DropEnum(PgEnum pgEnum) {
        this.pgEnum = pgEnum;
    }

    @Override
    public String getQuery() {
        return "DROP TYPE \"" + pgEnum.getName() + "\";";
    }
}
