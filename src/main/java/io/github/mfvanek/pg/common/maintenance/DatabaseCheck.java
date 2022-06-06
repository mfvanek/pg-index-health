/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * An abstract check on database structure.
 *
 * @param <T> any database object associated with a table (table itself, index, column, constraint)
 * @author Ivan Vahrushev
 * @since 0.5.1
 * @see TableNameAware
 */
public interface DatabaseCheck<T extends TableNameAware> {

    /**
     * Gets the diagnostic - a rule related to the check.
     *
     * @return diagnostic
     * @see Diagnostic
     */
    @Nonnull
    Diagnostic getDiagnostic();

    /**
     * Executes the check in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of deviations from the specified rule
     * @see PgContext
     */
    @Nonnull
    List<T> check(@Nonnull PgContext pgContext);

    /**
     * Executes the check in the specified schemas.
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of deviations from the specified rule
     */
    @Nonnull
    default List<T> check(@Nonnull final Collection<? extends PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::check)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Executes the check in the public schema.
     *
     * @return list of deviations from the specified rule
     */
    @Nonnull
    default List<T> check() {
        return check(PgContext.ofPublic());
    }
}
