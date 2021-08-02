/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.model.PgContext;

import java.util.List;
import javax.annotation.Nonnull;

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
    @Nonnull
    List<String> logAll(@Nonnull Exclusions exclusions, @Nonnull PgContext pgContext);

    /**
     * Logs indexes and tables health with given exclusions in public schema.
     *
     * @param exclusions {@link Exclusions}
     * @return results of logging health indexes
     */
    @Nonnull
    default List<String> logAll(@Nonnull final Exclusions exclusions) {
        return logAll(exclusions, PgContext.ofPublic());
    }
}
