package com.scentbird.krendel.model.config;

/**
 * Target database properties.
 */
public class KrendelTargetConfig {

    /**
     * JDBC URL of the database where Krendel initialize target schema structure.
     */
    private String url;

    /**
     * Login username of the target database.
     */
    private String username;

    /**
     * Login password of the target database.
     */
    private String password;

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

}
