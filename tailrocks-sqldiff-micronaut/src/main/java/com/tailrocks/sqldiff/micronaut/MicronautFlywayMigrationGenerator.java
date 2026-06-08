package com.tailrocks.sqldiff.micronaut;

import com.tailrocks.sqldiff.core.MigrationGenerator;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationReport;
import com.tailrocks.sqldiff.output.FlywayMigrationGenerator;
import io.micronaut.configuration.jdbc.hikari.DatasourceConfiguration;
import io.micronaut.context.annotation.Requires;
import org.flywaydb.core.Flyway;

import jakarta.inject.Singleton;
import java.util.Collections;

@Singleton
@Requires(beans = Flyway.class)
public class MicronautFlywayMigrationGenerator implements MigrationGenerator {

    private final SqlDiffConfiguration krendelConfiguration;
    private final DatasourceConfiguration datasourceConfiguration;
    private final Flyway flyway;

    public MicronautFlywayMigrationGenerator(SqlDiffConfiguration krendelConfiguration,
                                             DatasourceConfiguration datasourceConfiguration,
                                             Flyway flyway) {
        this.krendelConfiguration = krendelConfiguration;
        this.datasourceConfiguration = datasourceConfiguration;
        this.flyway = flyway;
    }

    @Override
    public void generateMigrations(MigrationReport report) throws Exception {
        new FlywayMigrationGenerator(
                datasourceConfiguration.getUrl(),
                datasourceConfiguration.getUsername(),
                datasourceConfiguration.getPassword(),
                Collections.singletonList(flyway),
                krendelConfiguration.getMigration().getMetadata(),
                krendelConfiguration.getMigration().getOutputPath(),
                krendelConfiguration.getMigration().isCleanOutputPath()
        ).generateMigrations(report);
    }

}
