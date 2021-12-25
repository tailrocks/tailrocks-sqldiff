package com.tailrocks.sqldiff.core.postgres.migration.statement.constraints;

import com.tailrocks.sqldiff.core.postgres.model.PgPrimaryKey;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

/**
 * @author Efim Matytsin
 */
public class PrimaryKeyConstraintTypeAction implements AddConstraintTypeAction {
    private final PgPrimaryKey pgPrimaryKey;

    public PrimaryKeyConstraintTypeAction(PgPrimaryKey pgPrimaryKey) {
        this.pgPrimaryKey = pgPrimaryKey;
    }

    @Override
    public String getQuery() {
        return "PRIMARY KEY (" +
                StringUtils.join(pgPrimaryKey.getColumns().stream().map(pk -> pk.getName()).collect(Collectors.toList()), ", ") + ")";
    }
}
