package com.scentbird.krendel;

import com.tailrocks.sqldiff.core.postgres.SchemaReader;
import com.tailrocks.sqldiff.core.postgres.diff.Diff;
import com.tailrocks.sqldiff.core.postgres.diff.DiffOptions;
import com.tailrocks.sqldiff.core.postgres.diff.SchemaDiff;
import com.tailrocks.sqldiff.core.postgres.migration.Migration;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationItem;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationOptions;
import com.tailrocks.sqldiff.core.postgres.migration.MigrationReport;
import com.tailrocks.sqldiff.core.postgres.model.PgSchema;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestCase {

    private static final Logger log = LoggerFactory.getLogger(TestCase.class);

    /**
     * subfolder name from test/resources/cases
     */
    private String name;
    private String dbNameV1;
    private String dbNameV2;

    private String jdbcUrlV1;
    private String jdbcUrlV2;

    private PostgreSQLContainer<?> container;

    private PgSchema schemaV1;
    private PgSchema schemaV2;

    private Diff diff;
    private MigrationReport migrationReport;

    public TestCase(String name, PostgreSQLContainer<?> container) {
        if (name == null) {
            throw new IllegalArgumentException("`name` can not be null");
        }
        if (container == null) {
            throw new IllegalArgumentException("`container` can not be null");
        }

        this.name = name;
        this.dbNameV1 = name + "_v1";
        this.dbNameV2 = name + "_v2";
        this.container = container;

        init();
    }

    public void readSchema() {
        SchemaReader v1reader = new SchemaReader(jdbcUrlV1, container.getUsername(), container.getPassword());
        SchemaReader v2reader = new SchemaReader(jdbcUrlV2, container.getUsername(), container.getPassword());

        schemaV1 = v1reader.read("public");
        schemaV2 = v2reader.read("public");
    }

    public Diff generateDiff() {
        return generateDiff(null);
    }

    public Diff generateDiff(DiffOptions options) {
        if (schemaV1 == null) {
            throw new IllegalStateException("Source schema is not scanned, need to run readSchema first.");
        }
        if (schemaV2 == null) {
            throw new IllegalStateException("Target schema is not scanned, need to run readSchema first.");
        }

        SchemaDiff schemaDiff = options != null ? new SchemaDiff(options) : new SchemaDiff();

        diff = schemaDiff.diff(schemaV1, schemaV2);

        return diff;
    }

    public MigrationReport generateMigrationReport() {
        return generateMigrationReport(null);
    }

    public MigrationReport generateMigrationReport(@Nullable MigrationOptions options) {
        if (diff == null) {
            throw new IllegalStateException("Diff is null, need to run generateDiff first.");
        }
        migrationReport = new Migration(diff, options).generate();
        return migrationReport;
    }

    public List<MigrationItem> generateMigrations() {
        return generateMigrationReport().getMigrations();
    }

    public void migrateAndRefreshSchema() {
        if (diff == null) {
            throw new IllegalStateException("Diff is null, need to run generateMigrations first.");
        }

        List<String> queries = migrationReport.getMigrations().stream()
                .map(MigrationItem::getQuery)
                .collect(Collectors.toList());

        executeQuery(jdbcUrlV1, container.getUsername(), container.getPassword(), queries);

        readSchema();

        diff = null;
        migrationReport = null;
    }

    public static void executeQuery(String jdbcUrl, String username, String password, List<String> queries) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            for (String sql : queries) {
                log.debug("Executing:\n{}", sql);

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    log.debug(jdbcUrl + " executed: " + statement.execute());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String fileToString(String version) {
        String path = "cases/" + name + "/schema_" + version + ".sql";
        URL resource = TestCase.class.getClassLoader().getResource(path);
        if (resource == null) {
            log.warn("Could not load classpath init script: {}", path);
            throw new IllegalStateException("Could not load classpath script: " + path + ". Resource not found.");
        }
        try {
            return IOUtils.toString(resource, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void init() {
        log.debug("Init {}", name);

        jdbcUrlV1 = getJdbcUrl(dbNameV1);
        jdbcUrlV2 = getJdbcUrl(dbNameV2);

        String v1Script = fileToString("v1");
        initSchema(jdbcUrlV1, dbNameV1, v1Script);

        String v2Script = fileToString("v2");
        initSchema(jdbcUrlV2, dbNameV2, v2Script);
    }

    private String getJdbcUrl(String databaseName) {
        return "jdbc:postgresql://" + container.getContainerIpAddress() + ":" +
                container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT) + "/" + databaseName + "?loggerLevel=OFF";
    }

    private void initSchema(String jdbcUrl, String database, String query) {
        String dropDb = "DROP DATABASE IF EXISTS " + database + ";";
        String createDb = "CREATE DATABASE " + database + ";";

        log.debug("Creating {}", database);

        executeQuery(container.getJdbcUrl(), container.getUsername(), container.getPassword(), Arrays.asList(dropDb, createDb));

        log.debug("Migrating {}", database);

        executeQuery(jdbcUrl, container.getUsername(), container.getPassword(), Arrays.asList(query));
    }

}
