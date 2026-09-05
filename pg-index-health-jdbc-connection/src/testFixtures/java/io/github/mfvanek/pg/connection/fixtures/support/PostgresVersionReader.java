/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.fixtures.support;

import io.github.mfvanek.pg.connection.exception.PgSqlException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

/**
 * Utility class for retrieving the PostgreSQL version from a given data source.
 * <p>
 * This class provides a method to query the PostgreSQL server version
 * by executing the "show server_version" command on the provided {@code DataSource}.
 * The result is returned as a string representing the version.
 * <p>
 * This class is not intended to be instantiated.
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Establishes a connection to the database using the provided {@code DataSource}.</li>
 * <li>Executes a query to fetch the server version.</li>
 * <li>Handles SQL exceptions by wrapping them in a custom runtime exception.</li>
 * </ul>
 * <p>
 * Exceptions:
 * <ul>
 * <li>{@code PgSqlException} is thrown if any {@code SQLException} occurs during the operation.</li>
 * </ul>
 */
public final class PostgresVersionReader {

    private PostgresVersionReader() {
        throw new UnsupportedOperationException();
    }

    /**
     * Reads the PostgreSQL server version from the provided data source.
     *
     * @param dataSource the {@link DataSource} instance used to establish a connection
     *                   to the PostgreSQL database; must not be null.
     * @return the server version as a {@link String}, typically in the format "X.Y.Z".
     * @throws PgSqlException if any {@link SQLException} occurs during the operation.
     */
    public static String readVersion(final DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("show server_version")) {
                resultSet.next();
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }
}
