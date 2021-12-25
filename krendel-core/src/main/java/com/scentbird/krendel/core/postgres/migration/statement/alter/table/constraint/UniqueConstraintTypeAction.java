package com.scentbird.krendel.core.postgres.migration.statement.alter.table.constraint;

import com.scentbird.krendel.core.postgres.model.PgColumn;
import com.scentbird.krendel.core.postgres.model.PgUniqueConstraint;
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
