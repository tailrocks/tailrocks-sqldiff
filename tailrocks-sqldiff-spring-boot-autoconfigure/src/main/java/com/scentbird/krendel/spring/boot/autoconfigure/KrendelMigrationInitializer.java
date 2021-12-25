package com.scentbird.krendel.spring.boot.autoconfigure;

import com.scentbird.krendel.core.MigrationGenerator;
import com.scentbird.krendel.hibernate.DataSourceConfig;
import com.scentbird.krendel.output.DbVersionControl;
import com.scentbird.krendel.hibernate.KrendelMigrator;
import org.hibernate.boot.Metadata;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

public class KrendelMigrationInitializer implements InitializingBean {

    private final KrendelProperties krendelProperties;
    private final DataSourceProperties dataSourceProperties;
    private final ObjectProvider<DbVersionControl> dbVersionControlProvider;
    private final ObjectProvider<MigrationGenerator> migrationGeneratorProvider;

    public KrendelMigrationInitializer(KrendelProperties krendelProperties,
                                       DataSourceProperties dataSourceProperties,
                                       ObjectProvider<DbVersionControl> dbVersionControlProvider,
                                       ObjectProvider<MigrationGenerator> migrationGeneratorProvider) {
        this.krendelProperties = krendelProperties;
        this.dataSourceProperties = dataSourceProperties;
        this.dbVersionControlProvider = dbVersionControlProvider;
        this.migrationGeneratorProvider = migrationGeneratorProvider;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        KrendelMigrator krendelMigrator = new KrendelMigrator(
                new DataSourceConfig(
                        dataSourceProperties.getUrl(),
                        dataSourceProperties.getUsername(),
                        dataSourceProperties.getPassword()
                ),
                krendelProperties,
                dbVersionControlProvider.getIfAvailable()
        );

        Metadata hibernateMetadata = HibernateMetadataExtractor.INSTANCE.getMetadata();

        krendelMigrator.start(hibernateMetadata, migrationGeneratorProvider.getIfAvailable());
    }

}
