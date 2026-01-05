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
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    AbstractSkipTablesPredicate(final PgContext pgContext, final Collection<String> rawTableNamesToSkip) {
        this.fullyQualifiedTableNamesToSkip = prepareFullyQualifiedNamesToSkip(pgContext, rawTableNamesToSkip);
    }

    /**
     * Evaluates this predicate on the given {@code DbObject}.
     * <p>
     * Returns {@code false} if the {@code DbObject} is a {@link TableNameAware} instance
     * with a table name that matches any of the fully qualified table names to skip.
     * Otherwise, returns {@code true}.
     * </p>
     *
     * @param dbObject the database object to test; must be non-null
     * @return {@code false} if the {@code DbObject} matches a table name in the skip set, {@code true} otherwise
     */
    @Override
    public boolean test(final DbObject dbObject) {
        if (!fullyQualifiedTableNamesToSkip.isEmpty() && dbObject instanceof final TableNameAware t) {
            return !fullyQualifiedTableNamesToSkip.contains(t.getTableName().toLowerCase(Locale.ROOT));
        }
        return true;
    }

    /**
     * Prepares a set of fully qualified names to skip by enriching each raw name with schema information from the provided
     * PostgreSQL context and converting it to lowercase for case-insensitive matching.
     *
     * @param pgContext      the PostgreSQL context used to enrich each raw name with schema information; must be non-null
     * @param rawNamesToSkip the collection of raw names to skip; must be non-null
     * @return an unmodifiable {@link Set} of fully qualified names to skip, in lowercase
     * @throws NullPointerException if {@code pgContext} or {@code rawNamesToSkip} is null
     */
    static Set<String> prepareFullyQualifiedNamesToSkip(final PgContext pgContext,
                                                        final Collection<String> rawNamesToSkip) {
        Objects.requireNonNull(pgContext, "pgContext cannot be null");
        return Objects.requireNonNull(rawNamesToSkip, "rawNamesToSkip cannot be null")
            .stream()
            .map(pgContext::enrichWithSchema)
            .map(s -> s.toLowerCase(Locale.ROOT))
            .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Prepares a set of names to skip by converting each raw name to lowercase for case-insensitive matching.
     *
     * @param rawNamesToSkip the collection of raw names to skip; must be non-null
     * @return an unmodifiable {@link Set} of names to skip, in lowercase
     * @throws NullPointerException if {@code pgContext} or {@code rawNamesToSkip} is null
     */
    static Set<String> prepareNamesToSkip(final Collection<String> rawNamesToSkip) {
        return Objects.requireNonNull(rawNamesToSkip, "rawNamesToSkip cannot be null")
            .stream()
            .map(s -> s.toLowerCase(Locale.ROOT))
            .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Prepares a set containing a single name to skip, after validating that it is non-blank.
     *
     * @param rawNameToSkip the raw name to skip; must be non-null and non-blank
     * @param argumentName  the name of the argument being checked
     * @return a {@link Set} containing the single validated name to skip
     * @throws IllegalArgumentException if {@code rawNameToSkip} is blank
     * @throws NullPointerException     if {@code rawNameToSkip} is null
     */
    static Set<String> prepareSingleNameToSkip(final String rawNameToSkip, final String argumentName) {
        return Set.of(Validators.notBlank(rawNameToSkip, argumentName));
    }
}
