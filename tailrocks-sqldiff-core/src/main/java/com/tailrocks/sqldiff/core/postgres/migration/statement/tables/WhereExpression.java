package com.tailrocks.sqldiff.core.postgres.migration.statement.tables;

import com.tailrocks.sqldiff.core.postgres.migration.statement.Condition;
import com.tailrocks.sqldiff.core.postgres.migration.statement.Statement;

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
