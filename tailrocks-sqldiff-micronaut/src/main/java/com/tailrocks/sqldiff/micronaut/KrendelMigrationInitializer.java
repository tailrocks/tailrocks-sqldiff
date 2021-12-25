package com.tailrocks.sqldiff.micronaut;

import com.tailrocks.sqldiff.core.MigrationGenerator;
import com.tailrocks.sqldiff.hibernate.DataSourceConfig;
import com.tailrocks.sqldiff.output.DbVersionControl;
import com.tailrocks.sqldiff.hibernate.KrendelMigrator;
import io.micronaut.configuration.jdbc.hikari.DatasourceConfiguration;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;

import javax.inject.Singleton;
import java.util.Optional;

@Singleton
@Requires(beans = MetadataSources.class)
public class KrendelMigrationInitializer {

    private final Metadata metadata;
    private final KrendelConfiguration krendelConfiguration;
    private final DatasourceConfiguration datasourceConfiguration;
    private final DbVersionControl dbVersionControl;
    private final MigrationGenerator migrationGenerator;

    public KrendelMigrationInitializer(MetadataSources metadataSources,
                                       KrendelConfiguration krendelConfiguration,
                                       DatasourceConfiguration datasourceConfiguration,
                                       Optional<DbVersionControl> dbVersionControl,
                                       Optional<MigrationGenerator> migrationGenerator) {
        this.metadata = metadataSources.buildMetadata();
        this.krendelConfiguration = krendelConfiguration;
        this.datasourceConfiguration = datasourceConfiguration;
        this.dbVersionControl = dbVersionControl.orElse(null);
        this.migrationGenerator = migrationGenerator.orElse(null);
    }

    @EventListener
    void onStartup(StartupEvent event) throws Exception {
        KrendelMigrator krendelMigrator = new KrendelMigrator(
                new DataSourceConfig(
                        datasourceConfiguration.getUrl(),
                        datasourceConfiguration.getUsername(),
                        datasourceConfiguration.getPassword()
                ),
                krendelConfiguration,
                dbVersionControl
        );

        krendelMigrator.start(metadata, migrationGenerator);
    }

}
