package com.tailrocks.sqldiff.micronaut;

import com.tailrocks.sqldiff.core.MigrationGenerator;
import com.tailrocks.sqldiff.hibernate.DataSourceConfig;
import com.tailrocks.sqldiff.hibernate.SqlDiffMigrator;
import com.tailrocks.sqldiff.output.DbVersionControl;
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
public class SqlDiffMigrationInitializer {

    private final Metadata metadata;
    private final SqlDiffConfiguration krendelConfiguration;
    private final DatasourceConfiguration datasourceConfiguration;
    private final DbVersionControl dbVersionControl;
    private final MigrationGenerator migrationGenerator;

    public SqlDiffMigrationInitializer(MetadataSources metadataSources,
                                       SqlDiffConfiguration krendelConfiguration,
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
        SqlDiffMigrator sqlDiffMigrator = new SqlDiffMigrator(
                new DataSourceConfig(
                        datasourceConfiguration.getUrl(),
                        datasourceConfiguration.getUsername(),
                        datasourceConfiguration.getPassword()
                ),
                krendelConfiguration,
                dbVersionControl
        );

        sqlDiffMigrator.start(metadata, migrationGenerator);
    }

}
