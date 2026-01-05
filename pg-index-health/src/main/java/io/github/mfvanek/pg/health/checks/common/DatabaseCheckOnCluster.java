/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.common;

import io.github.mfvanek.pg.core.checks.common.CheckTypeAware;
import io.github.mfvanek.pg.core.checks.common.RawTypeAware;
import io.github.mfvanek.pg.core.checks.common.TopologyAware;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a check (or diagnostic) to be performed across the entire PostgreSQL cluster.
 *
 * @param <T> the type of the database object being checked, extending {@link DbObject}
 * @author Ivan Vakhrushev
 * @see CheckTypeAware
 * @see RawTypeAware
 * @see DbObject
 * @since 0.6.0
 */
public interface DatabaseCheckOnCluster<T extends DbObject> extends CheckTypeAware, TopologyAware, RawTypeAware<T> {

    /**
     * Executes the check in the specified schema.
     *
     * @param pgContext        check's context with the specified schema
     * @param exclusionsFilter predicate to filter out unnecessary results
     * @return list of deviations from the specified rule
     * @see PgContext
     */
    List<T> check(PgContext pgContext, Predicate<? super T> exclusionsFilter);

    /**
     * Executes the check in the specified schema without filtering results.
     *
     * @param pgContext check's context with the specified schema
     * @return list of deviations from the specified rule
     * @see PgContext
     */
    default List<T> check(final PgContext pgContext) {
        return check(pgContext, item -> true);
    }

    /**
     * Executes the check in the default schema without filtering results.
     *
     * @return list of deviations from the specified rule
     * @see PgContext#ofDefault()
     */
    default List<T> check() {
        return check(PgContext.ofDefault(), item -> true);
    }

    /**
     * Executes the check in the specified schemas.
     *
     * @param pgContexts       a set of contexts specifying schemas
     * @param exclusionsFilter predicate to filter out unnecessary results
     * @return list of deviations from the specified rule
     */
    default List<T> check(final Collection<PgContext> pgContexts, final Predicate<? super T> exclusionsFilter) {
        return pgContexts.stream()
            .map(ctx -> check(ctx, exclusionsFilter))
            .flatMap(List::stream)
            .toList();
    }
}
