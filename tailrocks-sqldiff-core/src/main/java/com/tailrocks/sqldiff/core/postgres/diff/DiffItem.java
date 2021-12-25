package com.scentbird.krendel.core.postgres.diff;

import com.scentbird.krendel.core.postgres.model.PgElement;
import org.jetbrains.annotations.Nullable;

public class DiffItem {

    public static DiffItem ofInsert(PgElement after) {
        return new DiffItem(DiffOperation.INSERT, null, after);
    }

    public static DiffItem ofRemove(PgElement before) {
        return new DiffItem(DiffOperation.REMOVE, before, null);
    }

    public static DiffItem ofChange(PgElement before, PgElement after) {
        return new DiffItem(DiffOperation.CHANGE, before, after);
    }

    private final DiffOperation operation;
    private final PgElement before;
    private final PgElement after;

    private DiffItem(DiffOperation operation,
                     @Nullable PgElement before,
                     @Nullable PgElement after) {
        this.operation = operation;
        this.before = before;
        this.after = after;
    }


    public DiffOperation getOperation() {
        return operation;
    }

    @Nullable
    public PgElement getBefore() {
        return before;
    }

    @Nullable
    public PgElement getAfter() {
        return after;
    }

}
