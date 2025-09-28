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

/**
 * A concrete implementation of the {@link PgConnectionFactory} interface.
 * Provides methods to create PostgreSQL-specific database connections and data sources.
 * This class utilizes internal helper methods to configure and construct {@link PgConnection}
 * and {@link DataSource} instances based on provided connection details.
 */
public class PgConnectionFactoryImpl implements PgConnectionFactory {

    private static final Logger LOGGER = Logger.getLogger(PgConnectionFactoryImpl.class.getName());

    /**
     * Default constructor for the {@code PgConnectionFactoryImpl} class.
     * Initializes an instance of the connection factory for creating PostgreSQL
     * database connections and data sources.
     */
    public PgConnectionFactoryImpl() {
        // explicitly declared constructor for javadoc
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgConnection forUrl(final String pgUrl,
                               final String userName,
                               final String password) {
        LOGGER.fine(() -> String.format(Locale.ROOT, "Creating %s with pgUrl = %s, userName = %s, password = %s",
            PgConnection.class.getSimpleName(), pgUrl, userName, "*****"));
        final DataSource dataSource = dataSourceFor(pgUrl, userName, password);
        return PgConnectionImpl.of(dataSource, PgHostImpl.ofUrl(pgUrl));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource dataSourceFor(final String pgUrl,
                                    final String userName,
                                    final String password) {
        return PgConnectionHelper.createDataSource(pgUrl, userName, password);
    }
}
