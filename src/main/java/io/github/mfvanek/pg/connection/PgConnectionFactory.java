/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;

public interface PgConnectionFactory {

    @Nonnull
    PgConnection forUrl(@Nonnull String pgUrl, @Nonnull String userName, @Nonnull String password);
}
