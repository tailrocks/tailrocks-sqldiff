package com.tailrocks.sqldiff.spring.boot.autoconfigure;

import com.tailrocks.sqldiff.output.SqlDiffFlywayVersionControl;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class SqlDiffFlywayDbVersionControl extends SqlDiffFlywayVersionControl implements InitializingBean {

    private final ObjectProvider<Flyway> flywayProvider;

    public SqlDiffFlywayDbVersionControl(SqlDiffProperties krendelProperties,
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
