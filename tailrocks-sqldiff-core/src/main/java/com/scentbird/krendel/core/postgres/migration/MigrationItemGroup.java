package com.scentbird.krendel.core.postgres.migration;

import com.scentbird.krendel.core.postgres.Postgres12TableLockLevel;
import com.scentbird.krendel.core.postgres.model.PgTable;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * @author Efim Matytsin
 */
public class MigrationItemGroup {
    protected final List<MigrationItem> migrations = new ArrayList<>();
    protected final List<ImmutablePair<PgTable, Postgres12TableLockLevel>> locks = new ArrayList<>();

    public void addMigration(MigrationItem item) {
        requireNonNull(item, "item` can not be null");
        for (Map.Entry<PgTable, Postgres12TableLockLevel> e: item.getLocks().entrySet()) {
            locks.add(new ImmutablePair<>(e.getKey(), e.getValue()));
        }
        migrations.add(item);
    }

    public boolean hasConflicts(MigrationItem item) {
        requireNonNull(item, "item` can not be null");
        for (ImmutablePair<PgTable, Postgres12TableLockLevel> t : locks) {
            for (Map.Entry<PgTable, Postgres12TableLockLevel> e : item.getLocks().entrySet()) {
                if (t.getLeft().getName().equals(e.getKey().getName()) && t.getRight().hasConflictWith(e.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getDescription() {
        StringBuilder description = new StringBuilder();
        for (ImmutablePair<PgTable, Postgres12TableLockLevel> lock: locks) {
            description.append(lock.getLeft().getName());
            description.append(" with lock ");
            description.append(lock.getRight().name());
            description.append("\n");
        }
        return description.toString();
    }

    public List<MigrationItem> getMigrations() {
        return new ArrayList<>(migrations);
    }

    public boolean isEmpty() {
        return migrations.isEmpty();
    }

    public List<ImmutablePair<PgTable, Postgres12TableLockLevel>> getLocks() {
        return locks;
    }
}
