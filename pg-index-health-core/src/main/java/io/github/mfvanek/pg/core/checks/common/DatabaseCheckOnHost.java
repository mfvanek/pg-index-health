/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.common;

import io.github.mfvanek.pg.connection.host.HostAware;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * A check on database structure on a specific host.
 *
 * @param <T> represents an object in a database associated with a table
 * @author Ivan Vakhrushev
 * @see DbObject
 * @since 0.6.0
 */
public interface DatabaseCheckOnHost<T extends DbObject> extends DiagnosticAware, CheckTypeAware, RawTypeAware<T>, HostAware {

    /**
     * Executes the check in the specified schema.
     *
     * @param pgContext        check's context with the specified schema
     * @param exclusionsFilter predicate to filter out unnecessary results
     * @return list of deviations from the specified rule
     * @see PgContext
     */
    @Nonnull
    List<T> check(@Nonnull PgContext pgContext, @Nonnull Predicate<? super T> exclusionsFilter);

    /**
     * Executes the check in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of deviations from the specified rule
     * @see PgContext
     */
    @Nonnull
    default List<T> check(@Nonnull final PgContext pgContext) {
        return check(pgContext, item -> true);
    }

    /**
     * Executes the check in the public schema.
     *
     * @return list of deviations from the specified rule
     * @see PgContext#ofDefault()
     */
    @Nonnull
    default List<T> check() {
        return check(item -> true);
    }

    /**
     * Executes the check in the public schema.
     *
     * @param exclusionsFilter predicate to filter out unnecessary results
     * @return list of deviations from the specified rule
     */
    @Nonnull
    default List<T> check(@Nonnull final Predicate<? super T> exclusionsFilter) {
        return check(PgContext.ofDefault(), exclusionsFilter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isRuntime() {
        return getDiagnostic().isRuntime();
    }
}
