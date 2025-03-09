/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.host.PgHostImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

public class PgConnectionFactoryImpl implements PgConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PgConnectionFactoryImpl.class);

    @Nonnull
    @Override
    public PgConnection forUrl(@Nonnull final String pgUrl,
                               @Nonnull final String userName,
                               @Nonnull final String password) {
        LOGGER.debug("Creating {} with pgUrl = {}, userName = {}, password = {}",
            PgConnection.class.getSimpleName(), pgUrl, userName, "*****");
        final DataSource dataSource = dataSourceFor(pgUrl, userName, password);
        return PgConnectionImpl.of(dataSource, PgHostImpl.ofUrl(pgUrl));
    }

    @Override
    @Nonnull
    public DataSource dataSourceFor(@Nonnull final String pgUrl,
                                    @Nonnull final String userName,
                                    @Nonnull final String password) {
        return PgConnectionHelper.createDataSource(pgUrl, userName, password);
    }
}
