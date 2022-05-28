/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

public class PrimaryHostDeterminerImpl implements PrimaryHostDeterminer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryHostDeterminerImpl.class);
    private static final String SQL_QUERY = "select not pg_is_in_recovery()";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrimary(@Nonnull final PgConnection pgConnection) {
        Objects.requireNonNull(pgConnection, "pgConnection");
        LOGGER.debug("Executing on host {} query: {}", pgConnection.getHost(), SQL_QUERY);
        if (pgConnection.getHost().cannotBePrimary()) {
            return false;
        }

        final DataSource dataSource = pgConnection.getDataSource();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(SQL_QUERY)) {
                resultSet.next();
                final boolean executionResult = resultSet.getBoolean(1);
                LOGGER.debug("Query completed with result {}", executionResult);
                return executionResult;
            }
        } catch (SQLException e) {
            LOGGER.trace("Query failed", e);
            throw new RuntimeException(e); //NOSONAR
        }
    }
}
