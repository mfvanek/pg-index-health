/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.health.logger;

import io.github.mfvanek.pg.model.PgContext;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An abstraction of indexes health logger.
 *
 * @author Ivan Vakhrushev
 */
public interface IndexesHealthLogger {

    /**
     * Logs indexes health with given exclusions in given schema.
     *
     * @param exclusions {@link Exclusions}
     * @param pgContext  {@link PgContext}
     * @return results of logging health indexes
     */
    @Nonnull
    List<String> logAll(@Nonnull Exclusions exclusions, @Nonnull PgContext pgContext);

    /**
     * Logs indexes health with given exclusions in public schema.
     *
     * @param exclusions {@link Exclusions}
     * @return results of logging health indexes
     */
    @Nonnull
    default List<String> logAll(@Nonnull final Exclusions exclusions) {
        return logAll(exclusions, PgContext.ofPublic());
    }
}
