package com.scentbird.krendel.spring.boot.autoconfigure;

import com.tailrocks.sqldiff.output.KrendelFlywayVersionControl;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class KrendelFlywayDbVersionControl extends KrendelFlywayVersionControl implements InitializingBean {

    private final ObjectProvider<Flyway> flywayProvider;

    public KrendelFlywayDbVersionControl(KrendelProperties krendelProperties,
                                         DataSourceProperties dataSourceProperties,
                                         ObjectProvider<Flyway> flywayProvider) {
        super(krendelProperties, dataSourceProperties.getUrl());
        this.flywayProvider = flywayProvider;
    }

    @Override
    public void afterPropertiesSet() {
        Flyway flyway = flywayProvider.getIfAvailable();

        start(flyway);
    }

}
