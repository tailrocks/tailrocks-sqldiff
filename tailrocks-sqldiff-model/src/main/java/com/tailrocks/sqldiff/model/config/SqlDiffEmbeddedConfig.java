package com.tailrocks.sqldiff.model.config;

public abstract class SqlDiffEmbeddedConfig {

    /**
     * Whether to enable Krendel migrations. Enabled by default.
     */
    private boolean enabled = true;

    /**
     * Whether to exit application after Krendel migrations was generated. Enabled by default.
     */
    private boolean exitAfterFinish = true;

    /**
     * Path where to dump Hibernate DDL schema during preparing diff by Krendel. By default is {@literal null} and
     * represent a random temp file, which will be created for SchemaExport execution.
     */
    private String hibernateDdlDumpFile;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isExitAfterFinish() {
        return exitAfterFinish;
    }

    public void setExitAfterFinish(boolean exitAfterFinish) {
        this.exitAfterFinish = exitAfterFinish;
    }

    public String getHibernateDdlDumpFile() {
        return hibernateDdlDumpFile;
    }

    public void setHibernateDdlDumpFile(String hibernateDdlDumpFile) {
        this.hibernateDdlDumpFile = hibernateDdlDumpFile;
    }

    public abstract SqlDiffTargetConfig getTarget();

    public abstract SqlDiffFlywayConfig getFlyway();

    public abstract SqlDiffMigrationConfig getMigration();

    public abstract SqlDiffDiffConfig getDiff();

}
