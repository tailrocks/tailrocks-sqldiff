package com.tailrocks.sqldiff.core.postgres.migration.statement.sequences;

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
