/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

public class PgConnectionFactoryImpl implements PgConnectionFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PgConnectionFactoryImpl.class);

    @Nonnull
    @Override
    public PgConnection forUrl(@Nonnull String pgUrl, @Nonnull String userName, @Nonnull String password) {
        LOGGER.debug("Creating {} with pgUrl = {}, userName = {}, password = {}",
                PgConnection.class.getSimpleName(), pgUrl, userName, "*****");
        final var dataSource = PgConnectionHelper.createDataSource(pgUrl, userName, password);
        return PgConnectionImpl.of(dataSource, PgHostImpl.of(pgUrl));
    }
}
