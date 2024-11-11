/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Abstract base class for predicates that skip specific database tables.
 * <p>
 * This class provides a mechanism to test whether a {@link DbObject} represents
 * a table that should be skipped, based on a set of fully qualified table names.
 * Subclasses can define specific sets of table names to be skipped by providing
 * them at instantiation.
 * </p>
 *
 * @author Ivan Vakhrushev
 * @see Predicate
 * @since 0.13.3
 */
@Immutable
@ThreadSafe
abstract class AbstractSkipTablesPredicate implements Predicate<DbObject> {

    /**
     * A set of fully qualified table names that should be skipped.
     */
    private final Set<String> fullyQualifiedTableNamesToSkip;

    /**
     * Constructs an {@code AbstractSkipTablesPredicate} with the given schema context
     * and a collection of raw table names to skip.
     * <p>
     * The provided table names are enriched with the schema from {@link PgContext}
     * to ensure that fully qualified names are used for comparison.
     * </p>
     *
     * @param pgContext           the schema context used to enrich table names
     * @param rawTableNamesToSkip the collection of raw table names to skip, without schema enrichment
     * @throws NullPointerException if {@code pgContext} or {@code rawTableNamesToSkip} is null
     */
    AbstractSkipTablesPredicate(@Nonnull final PgContext pgContext, @Nonnull final Collection<String> rawTableNamesToSkip) {
        Objects.requireNonNull(pgContext, "pgContext cannot be null");
        this.fullyQualifiedTableNamesToSkip = Objects.requireNonNull(rawTableNamesToSkip, "rawTableNamesToSkip cannot be null")
            .stream()
            .map(pgContext::enrichWithSchema)
            .map(s -> s.toLowerCase(Locale.ROOT))
            .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Evaluates this predicate on the given {@code DbObject}.
     * <p>
     * Returns {@code false} if the {@code DbObject} is a {@link TableNameAware} instance
     * with a table name that matches any of the fully qualified table names to skip.
     * Otherwise, returns {@code true}.
     * </p>
     *
     * @param dbObject the object to be tested
     * @return {@code false} if the {@code DbObject} matches a table name in the skip set, {@code true} otherwise
     */
    @Override
    public boolean test(@Nonnull final DbObject dbObject) {
        if (!fullyQualifiedTableNamesToSkip.isEmpty() && dbObject instanceof TableNameAware) {
            final TableNameAware t = (TableNameAware) dbObject;
            return !fullyQualifiedTableNamesToSkip.contains(t.getTableName().toLowerCase(Locale.ROOT));
        }
        return true;
    }
}
