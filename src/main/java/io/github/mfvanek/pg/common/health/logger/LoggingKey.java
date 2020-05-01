/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import javax.annotation.Nonnull;

@SuppressWarnings("WeakerAccess")
public interface LoggingKey {

    @Nonnull
    String getKeyName();

    @Nonnull
    String getSubKeyName();
}
