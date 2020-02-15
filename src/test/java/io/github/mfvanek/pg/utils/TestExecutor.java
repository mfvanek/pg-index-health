/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.model.PgContext;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface TestExecutor {

    void execute(@Nonnull PgContext pgContext);
}
