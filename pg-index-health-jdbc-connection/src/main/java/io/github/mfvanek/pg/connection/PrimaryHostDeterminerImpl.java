/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.connection.exception.PgSqlException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Standard implementation of a service that determines if a given database connection is established with a primary host.
 */
public class PrimaryHostDeterminerImpl implements PrimaryHostDeterminer {

    private static final Logger LOGGER = Logger.getLogger(PrimaryHostDeterminerImpl.class.getName());
    private static final String SQL_QUERY = "select not pg_is_in_recovery()";

    /**
     * Default constructor for the PrimaryHostDeterminerImpl class.
     * <p>
     * Initializes a new instance of the PrimaryHostDeterminerImpl class,
     * which is a standard implementation of the {@code PrimaryHostDeterminer} interface.
     */
    public PrimaryHostDeterminerImpl() {
        // explicitly declared constructor for javadoc
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrimary(final PgConnection pgConnection) {
        Objects.requireNonNull(pgConnection, "pgConnection cannot be null");
        LOGGER.fine(() -> String.format(Locale.ROOT, "Executing on host %s query: %s", pgConnection.getHost(), SQL_QUERY));

        if (pgConnection.getHost().cannotBePrimary()) {
            return false;
        }

        final DataSource dataSource = pgConnection.getDataSource();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(SQL_QUERY)) {
                resultSet.next();
                final boolean executionResult = resultSet.getBoolean(1);
                LOGGER.fine(() -> "Query completed with result " + executionResult);
                return executionResult;
            }
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }
}
