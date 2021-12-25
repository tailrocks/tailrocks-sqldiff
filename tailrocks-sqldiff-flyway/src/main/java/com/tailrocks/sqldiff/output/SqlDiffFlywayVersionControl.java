package com.tailrocks.sqldiff.output;

import com.tailrocks.sqldiff.model.config.SqlDiffEmbeddedConfig;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.jetbrains.annotations.Nullable;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;

import static java.util.Objects.requireNonNull;

public class SqlDiffFlywayVersionControl implements DbVersionControl {

    private static final Logger log = LoggerFactory.getLogger(SqlDiffFlywayVersionControl.class);

    private final SqlDiffEmbeddedConfig sqlDiffEmbeddedConfig;
    private final String datasourceUrl;

    public SqlDiffFlywayVersionControl(SqlDiffEmbeddedConfig sqlDiffEmbeddedConfig, String datasourceUrl) {
        this.sqlDiffEmbeddedConfig = requireNonNull(sqlDiffEmbeddedConfig);
        this.datasourceUrl = requireNonNull(datasourceUrl);
    }

    public void start(@Nullable Flyway flyway) {
        if (flyway == null) {
            log.debug("Flyway bean not available");
            return;
        }

        if (Objects.equals(datasourceUrl, getUrl())) {
            throw new RuntimeException("tailrocks-sqldiff Flyway migrations DB can not be same with Spring DataSource");
        }

        // TODO compare testcontainers JDBC urls

        if (datasourceUrl.startsWith("jdbc:postgresql:") && getUrl().startsWith("jdbc:postgresql:")) {
            Properties datasourceJdbcProperties = Driver.parseURL(datasourceUrl, new Properties());
            Properties targetJdbcProperties = Driver.parseURL(getUrl(), new Properties());

            if (Objects.equals(datasourceJdbcProperties.getProperty("PGHOST"), targetJdbcProperties.getProperty("PGHOST")) &&
                    Objects.equals(datasourceJdbcProperties.getProperty("PGPORT"), targetJdbcProperties.getProperty("PGPORT")) &&
                    Objects.equals(datasourceJdbcProperties.getProperty("PGDBNAME"), targetJdbcProperties.getProperty("PGDBNAME"))) {
                throw new RuntimeException("tailrocks-sqldiff Flyway migrations DB can not be same with Spring DataSource");
            }
        }

        FluentConfiguration fluentConfiguration = cloneConfiguration(flyway.getConfiguration());

        log.debug("Initializing Flyway migrations");

        // replace data source credentials
        fluentConfiguration.dataSource(getUrl(), getUsername(), getPassword());

        Flyway clonedFlyway = fluentConfiguration.load();

        clonedFlyway.migrate();
    }

    @Override
    public String getUrl() {
        if (StringUtils.isEmpty(sqlDiffEmbeddedConfig.getFlyway().getUrl())) {
            throw new RuntimeException("krendel.flyway.url can not be empty");
        }
        return sqlDiffEmbeddedConfig.getFlyway().getUrl();
    }

    @Override
    public String getUsername() {
        if (StringUtils.isEmpty(sqlDiffEmbeddedConfig.getFlyway().getUsername())) {
            throw new RuntimeException("krendel.flyway.username can not be empty");
        }
        return sqlDiffEmbeddedConfig.getFlyway().getUsername();
    }

    @Override
    public String getPassword() {
        return sqlDiffEmbeddedConfig.getFlyway().getPassword();
    }

    private FluentConfiguration cloneConfiguration(Configuration originalConfiguration) {
        FluentConfiguration configuration = new FluentConfiguration(originalConfiguration.getClassLoader());
        configuration.configuration(originalConfiguration);
        return configuration;
    }

}
