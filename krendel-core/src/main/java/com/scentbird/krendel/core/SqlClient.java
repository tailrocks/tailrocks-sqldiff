package com.scentbird.krendel.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlClient {

    @FunctionalInterface
    public interface WithConnection {
        void accept(Connection connection) throws SQLException;
    }

    @FunctionalInterface
    public interface WithResultSet {
        void accept(ResultSet rs) throws SQLException;
    }

    public static class UncheckedSQLException extends RuntimeException {
        public UncheckedSQLException(SQLException cause) {
            super(cause);
        }
    }

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public SqlClient(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public void doWithConnection(WithConnection withConnection) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            withConnection.accept(connection);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public void executeQuery(Connection connection, String query, WithResultSet withResultSet) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    withResultSet.accept(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public int executeUpdate(Connection connection, String query) {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

    public void executeQuery(String query, WithResultSet withResultSet) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            executeQuery(connection, query, withResultSet);
        } catch (SQLException e) {
            throw new UncheckedSQLException(e);
        }
    }

}
