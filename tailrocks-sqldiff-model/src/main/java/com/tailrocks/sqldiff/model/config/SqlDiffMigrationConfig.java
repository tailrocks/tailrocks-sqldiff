package com.tailrocks.sqldiff.model.config;

public abstract class KrendelMigrationConfig {

    /**
     * Path where to put generated migrations.
     */
    private String outputPath;

    /**
     * Whether to delete all files in the output path before save new migrations. Disabled by default.
     */
    private boolean cleanOutputPath = false;

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public boolean isCleanOutputPath() {
        return cleanOutputPath;
    }

    public void setCleanOutputPath(boolean cleanOutputPath) {
        this.cleanOutputPath = cleanOutputPath;
    }

    public abstract SqlDiffMigrationMetadataConfig getMetadata();

}
