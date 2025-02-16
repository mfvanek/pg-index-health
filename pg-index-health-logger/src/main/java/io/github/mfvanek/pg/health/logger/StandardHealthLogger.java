/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.factory.ConnectionCredentials;
import io.github.mfvanek.pg.connection.factory.HighAvailabilityPgConnectionFactory;

import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * Outputs summary about database health to an array of strings.
 *
 * @author Ivan Vakhrushev
 */
public class StandardHealthLogger extends AbstractHealthLogger {

    public StandardHealthLogger(@Nonnull final ConnectionCredentials credentials,
                                @Nonnull final HighAvailabilityPgConnectionFactory connectionFactory,
                                @Nonnull final Function<HighAvailabilityPgConnection, DatabaseChecksOnCluster> databaseChecksFactory) {
        super(credentials, connectionFactory, databaseChecksFactory);
    }

    @Override
    protected String writeToLog(@Nonnull final LoggingKey key, final int value) {
        return key.getSubKeyName() + ":" + value;
    }
}
