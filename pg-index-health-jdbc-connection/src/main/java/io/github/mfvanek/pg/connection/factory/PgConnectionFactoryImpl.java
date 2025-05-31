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

import java.util.Locale;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

public class PgConnectionFactoryImpl implements PgConnectionFactory {

    private static final Logger LOGGER = Logger.getLogger(PgConnectionFactoryImpl.class.getName());

    @Nonnull
    @Override
    public PgConnection forUrl(@Nonnull final String pgUrl,
                               @Nonnull final String userName,
                               @Nonnull final String password) {
        LOGGER.fine(() -> String.format(Locale.ROOT, "Creating %s with pgUrl = %s, userName = %s, password = %s",
            PgConnection.class.getSimpleName(), pgUrl, userName, "*****"));
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
