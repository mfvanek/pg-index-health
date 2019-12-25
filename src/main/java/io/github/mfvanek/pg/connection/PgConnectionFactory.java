/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;

public interface PgConnectionFactory {

    @Nonnull
    PgConnection forUrl(@Nonnull String pgUrl, @Nonnull String userName, @Nonnull String password);
}
