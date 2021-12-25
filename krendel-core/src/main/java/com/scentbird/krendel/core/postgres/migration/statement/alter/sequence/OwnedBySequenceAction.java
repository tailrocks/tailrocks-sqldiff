package com.scentbird.krendel.core.postgres.migration.statement.alter.sequence;

/**
 * @author Efim Matytsin
 */
public class OwnedBySequenceAction implements AlterSequenceAction {
    private final String name;

    public OwnedBySequenceAction(String name) {
        this.name = name;
    }

    @Override
    public String getQuery() {
        return "OWNED BY " + name;
    }
}
