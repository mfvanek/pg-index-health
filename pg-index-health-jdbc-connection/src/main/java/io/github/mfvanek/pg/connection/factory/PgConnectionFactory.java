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

import javax.annotation.Nonnull;
import javax.sql.DataSource;

public interface PgConnectionFactory {

    @Nonnull
    PgConnection forUrl(@Nonnull String pgUrl, @Nonnull String userName, @Nonnull String password);

    @Nonnull
    DataSource dataSourceFor(@Nonnull String pgUrl, @Nonnull String userName, @Nonnull String password);
}
