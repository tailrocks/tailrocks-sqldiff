package com.tailrocks.sqldiff.core.postgres.migration.statement.constraints;

import com.tailrocks.sqldiff.core.postgres.model.PgColumn;
import com.tailrocks.sqldiff.core.postgres.model.PgUniqueConstraint;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

/**
 * @author Efim Matytsin
 */
public class UniqueConstraintTypeAction implements AddConstraintTypeAction {
    private final PgUniqueConstraint uniqueConstraint;

    public UniqueConstraintTypeAction(PgUniqueConstraint uniqueConstraint) {
        this.uniqueConstraint = uniqueConstraint;
    }

    @Override
    public String getQuery() {
        return "UNIQUE (" +
                StringUtils.join(uniqueConstraint.getColumns().stream().map(PgColumn::getName).collect(Collectors.toList()), ", ") + ")";
    }
}
