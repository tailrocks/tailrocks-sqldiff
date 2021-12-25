package com.scentbird.krendel.micronaut;

import com.scentbird.krendel.output.KrendelFlywayVersionControl;
import io.micronaut.configuration.jdbc.hikari.DatasourceConfiguration;
import io.micronaut.context.annotation.Requires;
import org.flywaydb.core.Flyway;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Requires(beans = Flyway.class)
public class KrendelFlywayDbVersionControl extends KrendelFlywayVersionControl {

    private final Flyway flyway;

    public KrendelFlywayDbVersionControl(KrendelConfiguration krendelConfiguration,
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
