package com.tailrocks.sqldiff.micronaut;

import com.tailrocks.sqldiff.output.SqlDiffFlywayVersionControl;
import io.micronaut.configuration.jdbc.hikari.DatasourceConfiguration;
import io.micronaut.context.annotation.Requires;
import org.flywaydb.core.Flyway;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Requires(beans = Flyway.class)
public class SqlDiffFlywayDbVersionControl extends SqlDiffFlywayVersionControl {

    private final Flyway flyway;

    public SqlDiffFlywayDbVersionControl(SqlDiffConfiguration krendelConfiguration,
                                         DatasourceConfiguration datasourceConfiguration,
                                         Optional<Flyway> flyway) {
        super(krendelConfiguration, datasourceConfiguration.getUrl());
        this.flyway = flyway.orElse(null);
    }

    @PostConstruct
    public void postConstruct() {
        start(flyway);
    }

}
