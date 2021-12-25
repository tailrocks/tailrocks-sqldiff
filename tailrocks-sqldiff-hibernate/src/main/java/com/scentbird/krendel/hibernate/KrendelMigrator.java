package com.scentbird.krendel.hibernate;

import com.tailrocks.sqldiff.core.KrendelCleaner;
import com.tailrocks.sqldiff.core.MigrationGenerator;
import com.tailrocks.sqldiff.core.postgres.diff.DiffOptions;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationOptions;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationReport;
import com.scentbird.krendel.model.config.KrendelDiffConfig;
import com.scentbird.krendel.model.config.KrendelEmbeddedConfig;
import com.scentbird.krendel.output.DbVersionControl;
import com.scentbird.krendel.output.KrendelOutput;
import org.hibernate.boot.Metadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ansi.AnsiStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Properties;

import static com.scentbird.krendel.output.KrendelOutput.consolePrintln;
import static org.springframework.boot.ansi.AnsiOutput.encode;

public class KrendelMigrator {

    private final static Logger log = LoggerFactory.getLogger(KrendelMigrator.class);

    private final DataSourceConfig dataSourceConfig;
    private final KrendelEmbeddedConfig krendelConfiguration;
    private final DbVersionControl dbVersionControl;

    public KrendelMigrator(DataSourceConfig dataSourceConfig,
                           KrendelEmbeddedConfig krendelEmbeddedConfig,
                           DbVersionControl dbVersionControl) {
        this.dataSourceConfig = dataSourceConfig;
        this.krendelConfiguration = krendelEmbeddedConfig;
        this.dbVersionControl = dbVersionControl;
    }

    public void start(@NotNull Metadata metadata, @Nullable MigrationGenerator migrationGenerator) throws Exception {
        log.info("Starting Krendel migrator");

        Objects.requireNonNull(metadata, "Metadata can not be null");

        if (!(dataSourceConfig.getUrl().startsWith("jdbc:postgresql:") ||
                dataSourceConfig.getUrl().startsWith("jdbc:tc:postgresql:"))) {
            throw new RuntimeException("DataSource url is not starts with jdbc:postgresql: or jdbc:tc:postgresql:");
        }

        if (krendelConfiguration.getTarget().getUrl() == null) {
            throw new RuntimeException("krendel.target.url is not set");
        }

        if (!(krendelConfiguration.getTarget().getUrl().startsWith("jdbc:postgresql:") ||
                krendelConfiguration.getTarget().getUrl().startsWith("jdbc:tc:postgresql:"))) {
            throw new RuntimeException("Krendel target url is not starts with jdbc:postgresql: or jdbc:postgresql:");
        }

        File hibernateDdlDumpFile = krendelConfiguration.getHibernateDdlDumpFile() != null ?
                new File(krendelConfiguration.getHibernateDdlDumpFile()) :
                File.createTempFile("hibernate-ddl-dump", ".sql");

        deleteFileIfExists(hibernateDdlDumpFile);

        runHibernateSchemaExport(metadata, hibernateDdlDumpFile);

        if (!hibernateDdlDumpFile.exists()) {
            throw new FileNotFoundException("Hibernate DDL dump was not created");
        }

        String sourceJdbcUrl = dbVersionControl != null
                ? dbVersionControl.getUrl() : dataSourceConfig.getUrl();
        String sourceJdbcUsername = dbVersionControl != null
                ? dbVersionControl.getUsername() : dataSourceConfig.getUsername();
        String sourceJdbcPassword = dbVersionControl != null
                ? dbVersionControl.getPassword() : dataSourceConfig.getPassword();

        if (Objects.equals(sourceJdbcUrl, krendelConfiguration.getTarget().getUrl())) {
            throw new RuntimeException("Krendel target DB can not be same with Spring DataSource");
        }

        // TODO compare testcontainers JDBC urls

        if (sourceJdbcUrl.startsWith("jdbc:postgresql:") &&
                krendelConfiguration.getTarget().getUrl().startsWith("jdbc:postgresql:")) {
            Properties datasourceJdbcProperties = Driver.parseURL(sourceJdbcUrl, new Properties());
            Properties targetJdbcProperties = Driver.parseURL(krendelConfiguration.getTarget().getUrl(), new Properties());

            if (Objects.equals(datasourceJdbcProperties.getProperty("PGHOST"), targetJdbcProperties.getProperty("PGHOST")) &&
                    Objects.equals(datasourceJdbcProperties.getProperty("PGPORT"), targetJdbcProperties.getProperty("PGPORT")) &&
                    Objects.equals(datasourceJdbcProperties.getProperty("PGDBNAME"), targetJdbcProperties.getProperty("PGDBNAME"))) {
                throw new RuntimeException("Krendel target DB can not be same with Spring DataSource");
            }
        }

        applyHibernateDdl(hibernateDdlDumpFile);

        KrendelOutput.printAppName();

        KrendelOutput krendelOutput = new KrendelOutput(
                sourceJdbcUrl,
                krendelConfiguration.getTarget().getUrl(),
                sourceJdbcUsername,
                sourceJdbcPassword,
                krendelConfiguration.getTarget().getUsername(),
                krendelConfiguration.getTarget().getPassword(),
                null,
                true
        );

        DiffOptions diffOptions = new DiffOptions();
        fillDiffOptions(diffOptions, krendelConfiguration);

        MigrationOptions migrationOptions = new MigrationOptions();
        fillMigrationOptions(migrationOptions, krendelConfiguration);

        MigrationReport migrationReport = krendelOutput.generateMigrationReport(diffOptions, migrationOptions);

        if (migrationGenerator != null
                && krendelConfiguration.getMigration() != null
                && krendelConfiguration.getMigration().getOutputPath() != null) {
            krendelOutput.printStep("Generating migration files");

            migrationGenerator.generateMigrations(migrationReport);

            consolePrintln("Migrations were exported to " + encode(AnsiStyle.BOLD) + krendelConfiguration.getMigration().getOutputPath());
        }

        if (krendelConfiguration.isExitAfterFinish()) {
            log.info("Shutting down application");

            // we use halt instead of proper shutdown because sometimes this can be launched not inside main thread
            // override exit status
            Runtime.getRuntime().halt(0);
        }
    }

    private void applyHibernateDdl(File hibernateDdlDumpFile) throws IOException {
        String hibernateSql = new String(Files.readAllBytes(hibernateDdlDumpFile.toPath()));

        // clean target database before apply hibernate migrations
        KrendelCleaner krendelCleaner = new KrendelCleaner(
                krendelConfiguration.getTarget().getUrl(),
                krendelConfiguration.getTarget().getUsername(),
                krendelConfiguration.getTarget().getPassword()
        );
        krendelCleaner.deleteAll();

        executeSql(
                krendelConfiguration.getTarget().getUrl(),
                krendelConfiguration.getTarget().getUsername(),
                krendelConfiguration.getTarget().getPassword(),
                hibernateSql
        );
    }

    private void runHibernateSchemaExport(Metadata metadata, File outputFile) throws IOException {
        String outputPath = outputFile.getCanonicalPath();

        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setDelimiter(";");
        schemaExport.setFormat(false);
        schemaExport.setOutputFile(outputPath);

        schemaExport.execute(
                EnumSet.of(TargetType.SCRIPT),
                SchemaExport.Action.CREATE,
                metadata
        );

        log.info("Hibernate schema exported to file: {}", outputPath);
    }

    private void deleteFileIfExists(File dropAndCreateDdlFile) {
        if (dropAndCreateDdlFile.exists()) {
            if (!dropAndCreateDdlFile.isFile()) {
                String msg = MessageFormat.format("File is not a normal file {0}", dropAndCreateDdlFile);
                throw new IllegalStateException(msg);
            }

            if (!dropAndCreateDdlFile.delete()) {
                String msg = MessageFormat.format("Unable to delete file {0}", dropAndCreateDdlFile);
                throw new IllegalStateException(msg);
            }
        }
    }

    public static void executeSql(String jdbcUrl, String username, String password, String sql) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    private void fillDiffOptions(DiffOptions diffOptions, KrendelEmbeddedConfig krendelProperties) {
        KrendelDiffConfig.Ignore ignore = krendelProperties.getDiff().getIgnore();

        if (ignore.getExtensions() != null && !ignore.getExtensions().isEmpty()) {
            diffOptions.ignoreExtensions(ignore.getExtensions());
        }
        if (ignore.getEnums() != null && !ignore.getEnums().isEmpty()) {
            diffOptions.ignoreEnums(ignore.getEnums());
        }
        if (ignore.getTables() != null && !ignore.getTables().isEmpty()) {
            diffOptions.ignoreTables(ignore.getTables());
        }
        if (ignore.getSequences() != null && !ignore.getSequences().isEmpty()) {
            diffOptions.ignoreSequences(ignore.getSequences());
        }
        if (ignore.getIndexes() != null && !ignore.getIndexes().isEmpty()) {
            diffOptions.ignoreIndexes(ignore.getIndexes());
        }
        if (ignore.getColumns() != null && !ignore.getColumns().isEmpty()) {
            diffOptions.ignoreColumns(ignore.getColumns());
        }
        if (ignore.getColumnsDefaultValue() != null && !ignore.getColumnsDefaultValue().isEmpty()) {
            diffOptions.ignoreColumnsDefaultValue(ignore.getColumnsDefaultValue());
        }
        if (ignore.getViews() != null && !ignore.getViews().isEmpty()) {
            diffOptions.ignoreViews(ignore.getViews());
        }
        if (ignore.getConstraints() != null && !ignore.getConstraints().isEmpty()) {
            diffOptions.ignoreConstraints(ignore.getConstraints());
        }

        diffOptions.setForeignKeyCompareMethod(krendelProperties.getDiff().getForeignKeyCompareMethod());
    }

    private void fillMigrationOptions(MigrationOptions migrationOptions, KrendelEmbeddedConfig krendelProperties) {
        KrendelDiffConfig.Migration migration = krendelProperties.getDiff().getMigration();

        migrationOptions.setDropTableIfExists(migration.getTables().getDrop().isIfExists());

        migrationOptions.setAddColumnIfNotExists(migration.getColumns().getAdd().isIfNotExists());
        migrationOptions.setDropColumnIfExists(migration.getColumns().getDrop().isIfExists());

        migrationOptions.setCreateIndexIfNotExists(migration.getIndexes().getCreate().isIfNotExists());
        migrationOptions.setCreateIndexConcurrently(migration.getIndexes().getCreate().isConcurrently());
        migrationOptions.setDropIndexIfExists(migration.getIndexes().getDrop().isIfExists());
        migrationOptions.setDropIndexConcurrently(migration.getIndexes().getDrop().isConcurrently());

        migrationOptions.setDropSequenceIfExists(migration.getSequences().getDrop().isIfExists());

        migrationOptions.setSafeAddDefaultColumn(migration.getSafe().getAdd().isDefaultColumns());
        migrationOptions.setSafeAddNotNullColumn(migration.getSafe().getAdd().isNotNullColumns());
        migrationOptions.setSafeCreateForeignKey(migration.getSafe().getAdd().isForeignKeys());

        migrationOptions.setIgnoreHint(migration.isIgnoreHint());
    }

}
