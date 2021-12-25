package com.scentbird.krendel.core.postgres.migration.statement;

import java.util.Set;

/**
 * @author Efim Matytsin
 */
public class Statements {
    private final Set<Statement> statements;

    public Statements(Set<Statement> statements) {
        this.statements = statements;
    }

    public Set<Statement> getStatements() {
        return statements;
    }

    public String getQuery(){
        return statements.stream()
                .map(Statement::getQuery)
                .reduce((s, s2) -> s + "/n" + s2)
                .orElse(null);
    }
}
