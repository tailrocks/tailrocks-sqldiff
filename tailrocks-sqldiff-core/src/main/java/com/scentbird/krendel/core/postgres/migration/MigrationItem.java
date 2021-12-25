package com.scentbird.krendel.core.postgres.migration;

import com.scentbird.krendel.core.postgres.Postgres12TableLockLevel;
import com.scentbird.krendel.core.postgres.diff.DiffOperation;
import com.scentbird.krendel.core.postgres.model.PgTable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class MigrationItem {

    private final DiffOperation operation;
    private final String query;
    @Nullable
    private final String comment;
    private final Map<PgTable, Postgres12TableLockLevel> locks;

    public MigrationItem(DiffOperation operation, String query, Map<PgTable, Postgres12TableLockLevel> locks) {
        this(operation, query, null, locks);
    }

    public MigrationItem(DiffOperation operation, String query, @Nullable String comment, Map<PgTable, Postgres12TableLockLevel> locks) {
        Objects.requireNonNull(operation, "`operation` must not be null");
        Objects.requireNonNull(query, "`query` must not be null");
        this.operation = operation;
        this.query = query;
        this.comment = comment;
        this.locks = locks;
    }

    public DiffOperation getOperation() {
        return operation;
    }

    public String getQuery() {
        return query;
    }

    public Map<PgTable, Postgres12TableLockLevel> getLocks() {
        return locks;
    }

    public String getComment() {
        return comment;
    }

    public boolean isOperationInsert() {
        return operation == DiffOperation.INSERT;
    }

    public boolean isOperationRemove() {
        return operation == DiffOperation.REMOVE;
    }

    public boolean isOperationChange() {
        return operation == DiffOperation.CHANGE;
    }

}
