package com.scentbird.krendel.core.postgres.migration.statement;

import com.scentbird.krendel.core.postgres.model.PgEnum;

/**
 * @author Efim Matytsin
 */
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
