package com.tailrocks.sqldiff.core;

import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SqlDiffCleaner {

    private static final Logger log = LoggerFactory.getLogger(SqlDiffCleaner.class.getSimpleName());

    private final SqlClient sqlClient;
    private final String schema;

    public SqlDiffCleaner(String jdbcUrl, String username, String password) {
        this.sqlClient = new SqlClient(jdbcUrl, username, password);

        if (jdbcUrl.startsWith("jdbc:postgresql:")) {
            // standard JDBC postgresql driver
            Properties jdbcProperties = Driver.parseURL(jdbcUrl, new Properties());

            schema = jdbcProperties.getProperty("currentSchema", "public");
        } else if (jdbcUrl.startsWith("jdbc:tc:postgresql:")) {
            // testcontainers JDBC postgresql driver

            // TODO detect schema from testcontainers JDBC url
            schema = "public";
        } else {
            throw new UnsupportedOperationException("Unsupported JDBC url: " + jdbcUrl);
        }
    }

    public void deleteAll() {
        sqlClient.doWithConnection(connection -> {
            deleteAllExtensions(connection);
            deleteAllViews(connection);
            deleteAllTables(connection);
            deleteAllEnums(connection);
            deleteAllSequences(connection);
        });
    }

    public void deleteAllExtensions(Connection connection) {
        String sql = "SELECT DISTINCT e.extname\n" +
                "FROM pg_extension e\n" +
                "JOIN pg_namespace n ON (e.extnamespace = n.oid)\n" +
                "WHERE n.nspname = '" + schema + "';";

        StringBuilder dropQueryBuilder = new StringBuilder("DROP EXTENSION IF EXISTS ");

        List<String> extensionNames = new ArrayList<>();

        sqlClient.executeQuery(connection, sql, rs -> {
            String tableName = rs.getString(1);

            extensionNames.add("\"" + tableName + "\"");
        });

        if (extensionNames.isEmpty()) {
            return;
        }

        dropQueryBuilder.append(String.join(", ", extensionNames));

        dropQueryBuilder.append(" CASCADE");

        if (log.isDebugEnabled()) {
            log.debug("Deleting extensions: " + extensionNames);
        }

        sqlClient.executeUpdate(connection, dropQueryBuilder.toString());
    }

    public void deleteAllViews(Connection connection) {
        String sql = "SELECT viewname FROM pg_catalog.pg_views WHERE schemaname = '" + schema + "';";

        StringBuilder dropQueryBuilder = new StringBuilder("DROP VIEW IF EXISTS ");

        List<String> viewNames = new ArrayList<>();

        sqlClient.executeQuery(connection, sql, rs -> {
            String tableName = rs.getString(1);

            viewNames.add("\"" + tableName + "\"");
        });

        if (viewNames.isEmpty()) {
            return;
        }

        dropQueryBuilder.append(String.join(", ", viewNames));

        dropQueryBuilder.append(" CASCADE");

        if (log.isDebugEnabled()) {
            log.debug("Deleting views: " + viewNames);
        }

        sqlClient.executeUpdate(connection, dropQueryBuilder.toString());
    }

    public void deleteAllTables(Connection connection) {
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = '" + schema + "';";

        StringBuilder dropQueryBuilder = new StringBuilder("DROP TABLE IF EXISTS ");

        List<String> tableNames = new ArrayList<>();

        sqlClient.executeQuery(connection, sql, rs -> {
            String tableName = rs.getString(1);

            tableNames.add("\"" + tableName + "\"");
        });

        if (tableNames.isEmpty()) {
            return;
        }

        dropQueryBuilder.append(String.join(", ", tableNames));

        dropQueryBuilder.append(" CASCADE");

        if (log.isDebugEnabled()) {
            log.debug("Deleting tables: " + tableNames);
        }

        sqlClient.executeUpdate(connection, dropQueryBuilder.toString());
    }

    public void deleteAllEnums(Connection connection) {
        String sql = "SELECT DISTINCT t.typname\n" +
                "FROM pg_type t\n" +
                "JOIN pg_enum e ON t.oid = e.enumtypid\n" +
                "JOIN pg_namespace n ON n.oid = t.typnamespace\n" +
                "WHERE n.nspname = '" + schema + "';";

        StringBuilder dropQueryBuilder = new StringBuilder("DROP TYPE IF EXISTS ");

        List<String> enumNames = new ArrayList<>();

        sqlClient.executeQuery(connection, sql, rs -> {
            String enumName = rs.getString(1);

            enumNames.add("\"" + enumName + "\"");
        });

        if (enumNames.isEmpty()) {
            return;
        }

        dropQueryBuilder.append(String.join(", ", enumNames));

        dropQueryBuilder.append(" CASCADE");

        if (log.isDebugEnabled()) {
            log.debug("Deleting enums: " + enumNames);
        }

        sqlClient.executeUpdate(connection, dropQueryBuilder.toString());
    }

    public void deleteAllSequences(Connection connection) {
        String sql = "SELECT sequencename FROM pg_catalog.pg_sequences WHERE schemaname = '" + schema + "';";

        StringBuilder dropQueryBuilder = new StringBuilder("DROP SEQUENCE IF EXISTS ");

        List<String> enumNames = new ArrayList<>();

        sqlClient.executeQuery(connection, sql, rs -> {
            String enumName = rs.getString(1);

            enumNames.add("\"" + enumName + "\"");
        });

        if (enumNames.isEmpty()) {
            return;
        }

        dropQueryBuilder.append(String.join(", ", enumNames));

        dropQueryBuilder.append(" CASCADE");

        if (log.isDebugEnabled()) {
            log.debug("Deleting sequences: " + enumNames);
        }

        sqlClient.executeUpdate(connection, dropQueryBuilder.toString());
    }

}
