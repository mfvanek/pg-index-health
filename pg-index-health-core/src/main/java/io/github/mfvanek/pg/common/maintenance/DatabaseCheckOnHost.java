/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.PgContext;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * A check on database structure on a specific host.
 *
 * @param <T> represents an object in a database associated with a table
 * @author Ivan Vahrushev
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
     * @see PgContext#ofPublic()
     */
    @Nonnull
    default List<T> check() {
        return check(PgContext.ofPublic(), item -> true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default boolean isRuntime() {
        return getDiagnostic().isRuntime();
    }
}
