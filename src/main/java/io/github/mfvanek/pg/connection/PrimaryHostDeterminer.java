/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;

public interface PrimaryHostDeterminer {

    /**
     * Determines whether given connection is a connection to a primary host.
     *
     * @param pgConnection {@code PgConnection} object
     * @return {@code true} if this is a connection to a primary host.
     */
    boolean isPrimary(@Nonnull PgConnection pgConnection);
}
