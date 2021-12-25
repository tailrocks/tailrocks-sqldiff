package com.scentbird.krendel.core.postgres.migration.statement.update;

import com.scentbird.krendel.core.postgres.migration.statement.Statement;
import com.scentbird.krendel.core.postgres.model.PgColumn;
import com.scentbird.krendel.core.postgres.model.PgTable;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Efim Matytsin
 */
public class UpdateSet implements Statement {
    private final PgTable pgTable;
    private final Map<PgColumn, Statement> setList;
    private WhereExpression whereExpression;

    public UpdateSet(PgTable pgTable, Map<PgColumn, Statement> setList) {
        this.pgTable = pgTable;
        this.setList = setList;
    }

    public UpdateSet(PgTable pgTable, Map<PgColumn, Statement> setList, WhereExpression whereExpression) {
        this.pgTable = pgTable;
        this.setList = setList;
        this.whereExpression = whereExpression;
    }

    @Override
    public String getQuery() {
        String query = "UPDATE \"" + pgTable.getName() + "\"";
        StringBuilder setQuery = new StringBuilder("\nSET (");
        setQuery.append(setList.entrySet()
                .stream()
                .map(entry -> "\"" + entry.getKey().getName() + "\" = " + entry.getValue().getQuery())
                .collect(Collectors.joining(", "))
        );
        setQuery.append(")");

        query += setQuery;

        if (whereExpression != null) {
            query += "\n" + whereExpression.getQuery();
        }
        query += ";";
        return query;
    }
}
