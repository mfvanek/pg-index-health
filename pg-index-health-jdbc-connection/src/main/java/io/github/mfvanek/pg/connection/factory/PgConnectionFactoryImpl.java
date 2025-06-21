/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.host.PgHostImpl;

import java.util.Locale;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class PgConnectionFactoryImpl implements PgConnectionFactory {

    private static final Logger LOGGER = Logger.getLogger(PgConnectionFactoryImpl.class.getName());

    @Override
    public PgConnection forUrl(final String pgUrl,
                               final String userName,
                               final String password) {
        LOGGER.fine(() -> String.format(Locale.ROOT, "Creating %s with pgUrl = %s, userName = %s, password = %s",
            PgConnection.class.getSimpleName(), pgUrl, userName, "*****"));
        final DataSource dataSource = dataSourceFor(pgUrl, userName, password);
        return PgConnectionImpl.of(dataSource, PgHostImpl.ofUrl(pgUrl));
    }

    @Override
    public DataSource dataSourceFor(final String pgUrl,
                                    final String userName,
                                    final String password) {
        return PgConnectionHelper.createDataSource(pgUrl, userName, password);
    }
}
