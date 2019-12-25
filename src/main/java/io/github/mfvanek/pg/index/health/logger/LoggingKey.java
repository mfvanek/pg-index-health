/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.health.logger;

import javax.annotation.Nonnull;

@SuppressWarnings("WeakerAccess")
public interface LoggingKey {

    @Nonnull
    String getKeyName();

    @Nonnull
    String getSubKeyName();
}
