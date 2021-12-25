package com.tailrocks.sqldiff.spring.boot.autoconfigure;

import com.opencsv.exceptions.CsvValidationException;
import com.tailrocks.sqldiff.core.MigrationGenerator;
import com.tailrocks.sqldiff.core.postgres.SchemaReader;
import com.tailrocks.sqldiff.output.DbVersionControl;
import com.tailrocks.sqldiff.output.FlywayMigrationGenerator;
import org.flywaydb.core.Flyway;
import org.hibernate.boot.Metadata;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.Objects;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SchemaReader.class)
@ConditionalOnProperty(prefix = "krendel", name = "enabled", matchIfMissing = true)
@AutoConfigureAfter({HibernateJpaAutoConfiguration.class, FlywayAutoConfiguration.class})
@EnableConfigurationProperties({DataSourceProperties.class, KrendelProperties.class})
public class KrendelAutoConfiguration {

    /**
     * @param krendelProperties    Krendel configuration
     * @param entityManagerFactory add dependency on {@link EntityManagerFactory}, it needs for
     *                             {@link HibernateMetadataExtractor} to extract {@link Metadata} instance
     */
    @Bean
    @ConditionalOnMissingBean
    public KrendelMigrationInitializer krendelInitializer(
            ConfigurableApplicationContext applicationContext,
            KrendelProperties krendelProperties,
            DataSourceProperties dataSourceProperties,
            // can not delete this argument, we need entity manager initialized first to generate use Hibernate
            // SchemaExport
            EntityManagerFactory entityManagerFactory,
            ObjectProvider<DbVersionControl> dbVersionControl,
            ObjectProvider<MigrationGenerator> migrationGenerator
    ) {
        Objects.requireNonNull(entityManagerFactory);
        return new KrendelMigrationInitializer(
                krendelProperties,
                dataSourceProperties,
                dbVersionControl,
                migrationGenerator
        );
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(Flyway.class)
    @AutoConfigureAfter({FlywayAutoConfiguration.FlywayConfiguration.class})
    @ConditionalOnProperty(prefix = "krendel.flyway", name = "enabled", matchIfMissing = true)
    @EnableConfigurationProperties({DataSourceProperties.class, KrendelProperties.class})
    public static class KrendelFlywayMigrationsConfiguration {

        @Bean
        @ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", matchIfMissing = true)
        @ConditionalOnMissingBean
        public KrendelFlywayDbVersionControl krendelFlywayDbVersionControl(
                KrendelProperties krendelProperties,
                DataSourceProperties dataSourceProperties,
                ObjectProvider<Flyway> flyways
        ) {
            return new KrendelFlywayDbVersionControl(krendelProperties, dataSourceProperties, flyways);
        }

        @Bean
        @ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", matchIfMissing = true)
        @ConditionalOnMissingBean
        public FlywayMigrationGenerator krendelFlywayMigrationGenerator(
                KrendelProperties krendelProperties,
                DataSourceProperties dataSourceProperties,
                ObjectProvider<Flyway> flyways
        ) throws IOException, CsvValidationException {
            return new FlywayMigrationGenerator(
                    dataSourceProperties.getUrl(),
                    dataSourceProperties.getUsername(),
                    dataSourceProperties.getPassword(),
                    flyways,
                    krendelProperties.getMigration().getMetadata(),
                    krendelProperties.getMigration().getOutputPath(),
                    krendelProperties.getMigration().isCleanOutputPath()
            );
        }

    }

}
