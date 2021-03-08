/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;

import javax.annotation.Nonnull;

public interface DatabaseHealthFactory {

    @Nonnull
    DatabaseHealth of(@Nonnull HighAvailabilityPgConnection haPgConnection);
}
