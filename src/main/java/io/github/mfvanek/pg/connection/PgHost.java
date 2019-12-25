/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;

/**
 * An abstraction of database host. This is a real server where your queries will be executed.
 */
public interface PgHost {

    @Nonnull
    String getPgUrl();

    @Nonnull
    String getName();
}
