/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support;

/**
 * Functional interface for configuring a {@link DatabasePopulator} before database initialization.
 */
@FunctionalInterface
public interface DatabaseConfigurer {

    /**
     * Configures the given {@link DatabasePopulator}.
     *
     * @param databasePopulator the database populator to configure
     * @return the configured database populator
     */
    DatabasePopulator configure(DatabasePopulator databasePopulator);
}
