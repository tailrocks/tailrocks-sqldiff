package com.scentbird.krendel.core.postgres.migration;

public class MigrationOptions {

    // tables
    private boolean dropTableIfExists = false;

    // columns
    private boolean addColumnIfNotExists = false;
    private boolean dropColumnIfExists = false;

    // indexes
    private boolean createIndexIfNotExists = false;
    private boolean createIndexConcurrently = false;
    private boolean dropIndexIfExists = false;
    private boolean dropIndexConcurrently = false;

    // sequences
    private boolean dropSequenceIfExists = false;

    private boolean safeAddDefaultColumn = false;
    private boolean safeAddNotNullColumn = false;
    private boolean safeCreateForeignKey = false;

    /**
     * Generate list of ignore records (elements) based on drop statements.
     */
    private boolean ignoreHint;

    public boolean isDropTableIfExists() {
        return dropTableIfExists;
    }

    public MigrationOptions setDropTableIfExists(boolean dropTableIfExists) {
        this.dropTableIfExists = dropTableIfExists;
        return this;
    }

    public boolean isAddColumnIfNotExists() {
        return addColumnIfNotExists;
    }

    public MigrationOptions setAddColumnIfNotExists(boolean addColumnIfNotExists) {
        this.addColumnIfNotExists = addColumnIfNotExists;
        return this;
    }

    public boolean isDropColumnIfExists() {
        return dropColumnIfExists;
    }

    public MigrationOptions setDropColumnIfExists(boolean dropColumnIfExists) {
        this.dropColumnIfExists = dropColumnIfExists;
        return this;
    }

    public boolean isCreateIndexIfNotExists() {
        return createIndexIfNotExists;
    }

    public MigrationOptions setCreateIndexIfNotExists(boolean createIndexIfNotExists) {
        this.createIndexIfNotExists = createIndexIfNotExists;
        return this;
    }

    public boolean isCreateIndexConcurrently() {
        return createIndexConcurrently;
    }

    public MigrationOptions setCreateIndexConcurrently(boolean createIndexConcurrently) {
        this.createIndexConcurrently = createIndexConcurrently;
        return this;
    }

    public boolean isDropIndexIfExists() {
        return dropIndexIfExists;
    }

    public MigrationOptions setDropIndexIfExists(boolean dropIndexIfExists) {
        this.dropIndexIfExists = dropIndexIfExists;
        return this;
    }

    public boolean isDropIndexConcurrently() {
        return dropIndexConcurrently;
    }

    public MigrationOptions setDropIndexConcurrently(boolean dropIndexConcurrently) {
        this.dropIndexConcurrently = dropIndexConcurrently;
        return this;
    }

    public boolean isDropSequenceIfExists() {
        return dropSequenceIfExists;
    }

    public MigrationOptions setDropSequenceIfExists(boolean dropSequenceIfExists) {
        this.dropSequenceIfExists = dropSequenceIfExists;
        return this;
    }

    public boolean isSafeAddDefaultColumn() {
        return safeAddDefaultColumn;
    }

    public MigrationOptions setSafeAddDefaultColumn(boolean safeAddDefaultColumn) {
        this.safeAddDefaultColumn = safeAddDefaultColumn;
        return this;
    }

    public boolean isSafeAddNotNullColumn() {
        return safeAddNotNullColumn;
    }

    public MigrationOptions setSafeAddNotNullColumn(boolean safeAddNotNullColumn) {
        this.safeAddNotNullColumn = safeAddNotNullColumn;
        return this;
    }

    public boolean isSafeCreateForeignKey() {
        return safeCreateForeignKey;
    }

    public MigrationOptions setSafeCreateForeignKey(boolean safeCreateForeignKey) {
        this.safeCreateForeignKey = safeCreateForeignKey;
        return this;
    }

    public boolean isIgnoreHint() {
        return ignoreHint;
    }

    public MigrationOptions setIgnoreHint(boolean ignoreHint) {
        this.ignoreHint = ignoreHint;
        return this;
    }

}
