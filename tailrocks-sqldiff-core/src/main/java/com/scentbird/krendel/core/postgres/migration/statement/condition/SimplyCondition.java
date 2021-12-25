package com.scentbird.krendel.core.postgres.migration.statement.condition;

/**
 * @author Efim Matytsin
 */
@Deprecated
public class SimplyCondition implements Condition {
    private final String condition;

    public SimplyCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String getQuery() {
        return condition;
    }
}
