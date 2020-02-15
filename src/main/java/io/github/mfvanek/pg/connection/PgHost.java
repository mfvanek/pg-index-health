/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;

/**
 * An abstraction of database host. This is a real server where your queries will be executed.
 *
 * @author Ivan Vakhrushev
 */
public interface PgHost {

    @Nonnull
    String getPgUrl();

    @Nonnull
    String getName();
}
