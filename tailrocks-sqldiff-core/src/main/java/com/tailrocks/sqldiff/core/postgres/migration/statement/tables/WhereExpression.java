package com.scentbird.krendel.core.postgres.migration.statement.update;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;
import com.tailrocks.sqldiff.core.postgres.migration.statement.Condition;

/**
 * @author Efim Matytsin
 */
public class WhereExpression implements Statement {
    private final Condition condition;

    public WhereExpression(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String getQuery() {
        return "WHERE " + condition;
    }
}
