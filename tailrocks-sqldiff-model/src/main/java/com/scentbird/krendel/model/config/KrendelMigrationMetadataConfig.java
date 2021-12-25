package com.scentbird.krendel.model.config;

public class KrendelMigrationMetadataConfig {

    /**
     * JDBC url of production database to get metadata (table sizes).
     * It will help to generate smart migrations based on information from real database.
     */
    private String url;

    /**
     * Username of production database to get metadata.
     */
    private String username;

    /**
     * Password of production database to get metadata.
     */
    private String password;

    /**
     * Path to CSV file with metadata from production.
     */
    private String filePath;

    /**
     * Minimum number of rows in table that will cause to generate java migrations for unsafe sql statements.
     */
    private Long rowsCountThreshold;

    public KrendelMigrationMetadataConfig() {
    }

    public KrendelMigrationMetadataConfig(String url, String username, String password, Long rowsCountThreshold) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.rowsCountThreshold = rowsCountThreshold;
    }

    public KrendelMigrationMetadataConfig(String filePath, Long rowsCountThreshold) {
        this.filePath = filePath;
        this.rowsCountThreshold = rowsCountThreshold;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getRowsCountThreshold() {
        return rowsCountThreshold;
    }

    public void setRowsCountThreshold(Long rowsCountThreshold) {
        this.rowsCountThreshold = rowsCountThreshold;
    }

}
