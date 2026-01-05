/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.model.context.PgContext;

import java.util.List;

/**
 * An abstraction of indexes and tables health logger.
 *
 * @author Ivan Vakhrushev
 */
public interface HealthLogger {

    /**
     * Logs indexes and tables health with given exclusions in given schema.
     *
     * @param exclusions {@link Exclusions}
     * @param pgContext  {@link PgContext}
     * @return results of logging health indexes
     */
    List<String> logAll(Exclusions exclusions, PgContext pgContext);

    /**
     * Logs indexes and tables health with given exclusions in default schema.
     *
     * @param exclusions {@link Exclusions}
     * @return results of logging health indexes
     */
    default List<String> logAll(final Exclusions exclusions) {
        return logAll(exclusions, PgContext.ofDefault());
    }
}
