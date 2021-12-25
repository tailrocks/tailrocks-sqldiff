package com.scentbird.krendel.core.postgres;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * https://www.postgresql.org/docs/current/explicit-locking.html
 *
 * @author Efim Matytsin
 */
public enum Postgres12TableLockLevel {

    /**
     * Conflicts with the ACCESS EXCLUSIVE lock mode only.
     * The SELECT command acquires a lock of this mode on referenced tables.
     * In general, any query that only reads a table and does not modify it will acquire this lock mode.
     */
    ACCESS_SHARE(Arrays.asList("ACCESS_EXCLUSIVE")),

    /**
     * Conflicts with the EXCLUSIVE and ACCESS EXCLUSIVE lock modes.
     * The SELECT FOR UPDATE and SELECT FOR SHARE commands acquire a lock of this mode on the target table(s)
     * (in addition to ACCESS SHARE locks on any other tables that are referenced but not selected FOR UPDATE/FOR SHARE).
     */
    ROW_SHARE(Arrays.asList("EXCLUSIVE", "ACCESS_EXCLUSIVE")),

    /**
     * Conflicts with the SHARE, SHARE ROW EXCLUSIVE, EXCLUSIVE, and ACCESS EXCLUSIVE lock modes.
     * The commands UPDATE, DELETE, and INSERT acquire this lock mode on the target table (in addition to ACCESS SHARE locks on any other referenced tables).
     * In general, this lock mode will be acquired by any command that modifies data in a table.
     */
    ROW_EXCLUSIVE(Arrays.asList("SHARE", "SHARE_ROW_EXCLUSIVE", "EXCLUSIVE", "ACCESS_EXCLUSIVE")),

    /**
     * Conflicts with the SHARE UPDATE EXCLUSIVE, SHARE, SHARE ROW EXCLUSIVE, EXCLUSIVE, and ACCESS EXCLUSIVE lock modes.
     * This mode protects a table against concurrent schema changes and VACUUM runs.
     * Acquired by VACUUM (without FULL), ANALYZE, CREATE INDEX CONCURRENTLY, REINDEX CONCURRENTLY, CREATE STATISTICS, and certain ALTER INDEX and ALTER TABLE variants
     * (for full details see ALTER INDEX and ALTER TABLE).
     */
    SHARE_UPDATE_EXCLUSIVE(Arrays.asList("SHARE_UPDATE_EXCLUSIVE", "SHARE", "SHARE_ROW_EXCLUSIVE", "EXCLUSIVE", "ACCESS_EXCLUSIVE")),

    /**
     * Conflicts with the ROW EXCLUSIVE, SHARE UPDATE EXCLUSIVE, SHARE ROW EXCLUSIVE, EXCLUSIVE, and ACCESS EXCLUSIVE lock modes.
     * This mode protects a table against concurrent data changes.
     * Acquired by CREATE INDEX (without CONCURRENTLY).
     */
    SHARE(Arrays.asList("ROW_EXCLUSIVE", "SHARE_UPDATE_EXCLUSIVE", "SHARE_ROW_EXCLUSIVE", "EXCLUSIVE", "ACCESS_EXCLUSIVE")),

    /**
     * Conflicts with the ROW EXCLUSIVE, SHARE UPDATE EXCLUSIVE, SHARE, SHARE ROW EXCLUSIVE, EXCLUSIVE, and ACCESS EXCLUSIVE lock modes.
     * This mode protects a table against concurrent data changes, and is self-exclusive so that only one session can hold it at a time.
     * Acquired by CREATE TRIGGER and some forms of ALTER TABLE (see ALTER TABLE).
     */
    SHARE_ROW_EXCLUSIVE(Arrays.asList("ROW_EXCLUSIVE", "SHARE_UPDATE_EXCLUSIVE", "SHARE", "SHARE_ROW_EXCLUSIVE", "EXCLUSIVE", "ACCESS_EXCLUSIVE")),

    /**
     * Conflicts with the ROW SHARE, ROW EXCLUSIVE, SHARE UPDATE EXCLUSIVE, SHARE, SHARE ROW EXCLUSIVE, EXCLUSIVE, and ACCESS EXCLUSIVE lock modes.
     * This mode allows only concurrent ACCESS SHARE locks, i.e., only reads from the table can proceed in parallel with a transaction holding this lock mode.
     * Acquired by REFRESH MATERIALIZED VIEW CONCURRENTLY.
     */
    EXCLUSIVE(Arrays.asList("ROW_SHARE", "ROW_EXCLUSIVE", "SHARE_UPDATE_EXCLUSIVE", "SHARE", "SHARE_ROW_EXCLUSIVE", "EXCLUSIVE", "ACCESS_EXCLUSIVE")),

    /**
     * Conflicts with locks of all modes (ACCESS SHARE, ROW SHARE, ROW EXCLUSIVE, SHARE UPDATE EXCLUSIVE, SHARE, SHARE ROW EXCLUSIVE, EXCLUSIVE, and ACCESS EXCLUSIVE).
     * This mode guarantees that the holder is the only transaction accessing the table in any way.
     * Acquired by the DROP TABLE, TRUNCATE, REINDEX, CLUSTER, VACUUM FULL, and REFRESH MATERIALIZED VIEW (without CONCURRENTLY) commands. Many forms of ALTER INDEX and ALTER TABLE also acquire a lock at this level.
     * This is also the default lock mode for LOCK TABLE statements that do not specify a mode explicitly.
     */
    ACCESS_EXCLUSIVE(Arrays.asList("ACCESS_SHARE", "ROW_SHARE", "ROW_EXCLUSIVE", "SHARE_UPDATE_EXCLUSIVE", "SHARE", "SHARE_ROW_EXCLUSIVE", "EXCLUSIVE", "ACCESS_EXCLUSIVE"));

    private final Set<String> conflicts;

    Postgres12TableLockLevel(List<String> conflicts) {
        this.conflicts = new HashSet<>(conflicts);
    }

    public boolean hasConflictWith(Postgres12TableLockLevel level) {
        return this.conflicts.contains(level.name());
    }

}
