/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Predicate implementation for filtering database tables by specific names.
 * <p>
 * This class extends {@link AbstractSkipTablesPredicate} to create predicates that
 * exclude certain tables based on their names, as specified in the provided schema context.
 * </p>
 *
 * @author Ivan Vakhrushev
 * @see Predicate
 * @see AbstractSkipTablesPredicate
 * @since 0.13.3
 */
public final class SkipTablesByNamePredicate extends AbstractSkipTablesPredicate {

    private SkipTablesByNamePredicate(final PgContext ctx, final Collection<String> rawTableNamesToSkip) {
        super(ctx, rawTableNamesToSkip);
    }

    private SkipTablesByNamePredicate(final PgContext ctx, final String rawTableNameToSkip, final String... rawTableNamesToSkip) {
        this(ctx, collectNamesToSkip(rawTableNameToSkip, rawTableNamesToSkip, "rawTableNameToSkip", "rawTableNamesToSkip"));
    }

    /**
     * Creates a predicate to skip one or more tables in the default schema.
     *
     * @param rawTableNameToSkip  the name of the table to skip, must be non-null and non-blank
     * @param rawTableNamesToSkip names of other tables to skip, must be non-null and must not contain null or blank elements
     * @return a predicate that skips the specified tables in the default schema
     * @throws NullPointerException     if {@code rawTableNameToSkip}, {@code rawTableNamesToSkip} or any of its elements is null
     * @throws IllegalArgumentException if any of the table names is blank
     * @since 0.60.0
     */
    public static Predicate<DbObject> ofNames(final String rawTableNameToSkip, final String... rawTableNamesToSkip) {
        return new SkipTablesByNamePredicate(PgContext.ofDefault(), rawTableNameToSkip, rawTableNamesToSkip);
    }

    /**
     * Creates a predicate to skip specific tables in the default schema.
     *
     * @param rawTableNamesToSkip a collection of table names to skip, must be non-null
     * @return a predicate that skips the specified tables in the default schema
     * @throws NullPointerException if {@code rawTableNamesToSkip} is null
     */
    public static Predicate<DbObject> ofDefault(final Collection<String> rawTableNamesToSkip) {
        return new SkipTablesByNamePredicate(PgContext.ofDefault(), rawTableNamesToSkip);
    }

    /**
     * Creates a predicate to skip one or more tables in the specified schema context.
     *
     * @param ctx                 the schema context for the tables to be skipped, must be non-null
     * @param rawTableNameToSkip  the name of the table to skip, must be non-null and non-blank
     * @param rawTableNamesToSkip names of other tables to skip, must be non-null and must not contain null or blank elements
     * @return a predicate that skips the specified tables in the given schema
     * @throws NullPointerException     if {@code ctx}, {@code rawTableNameToSkip}, {@code rawTableNamesToSkip} or any of its elements is null
     * @throws IllegalArgumentException if any of the table names is blank
     * @since 0.60.0
     */
    public static Predicate<DbObject> ofNames(final PgContext ctx, final String rawTableNameToSkip, final String... rawTableNamesToSkip) {
        return new SkipTablesByNamePredicate(ctx, rawTableNameToSkip, rawTableNamesToSkip);
    }

    /**
     * Creates a predicate to skip specific tables in the specified schema context.
     *
     * @param ctx                 the schema context for the tables to be skipped, must be non-null
     * @param rawTableNamesToSkip a collection of table names to skip, must be non-null
     * @return a predicate that skips the specified tables in the given schema
     * @throws NullPointerException if {@code ctx} or {@code rawTableNamesToSkip} is null
     */
    public static Predicate<DbObject> of(final PgContext ctx, final Collection<String> rawTableNamesToSkip) {
        return new SkipTablesByNamePredicate(ctx, rawTableNamesToSkip);
    }
}
