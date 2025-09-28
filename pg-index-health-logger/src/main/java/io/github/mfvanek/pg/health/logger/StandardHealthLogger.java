/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.factory.ConnectionCredentials;
import io.github.mfvanek.pg.connection.factory.HighAvailabilityPgConnectionFactory;

import java.util.function.Function;

/**
 * Outputs summary about database health to an array of strings.
 *
 * @author Ivan Vakhrushev
 */
public class StandardHealthLogger extends AbstractHealthLogger {

    /**
     * Constructs an instance of {@code StandardHealthLogger}, which outputs a summary
     * of database health to an array of strings. This class extends {@code AbstractHealthLogger}.
     *
     * @param credentials the database connection credentials, including connection URLs, username, and password
     * @param connectionFactory the factory for creating high-availability PostgreSQL connections
     * @param databaseChecksFactory a factory function for generating database check operations on a cluster
     */
    public StandardHealthLogger(final ConnectionCredentials credentials,
                                final HighAvailabilityPgConnectionFactory connectionFactory,
                                final Function<HighAvailabilityPgConnection, DatabaseChecksOnCluster> databaseChecksFactory) {
        super(credentials, connectionFactory, databaseChecksFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String writeToLog(final LoggingKey key, final int value) {
        return key.getSubKeyName() + ":" + value;
    }
}
