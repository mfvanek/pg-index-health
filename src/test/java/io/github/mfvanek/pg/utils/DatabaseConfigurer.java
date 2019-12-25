/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.utils;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface DatabaseConfigurer {

    @Nonnull
    DatabasePopulator configure(@Nonnull DatabasePopulator databasePopulator);
}
