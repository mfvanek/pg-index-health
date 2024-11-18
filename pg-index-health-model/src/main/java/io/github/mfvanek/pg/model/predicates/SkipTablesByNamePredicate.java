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

import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.context.PgContext;

import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

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
@Immutable
@ThreadSafe
public final class SkipTablesByNamePredicate extends AbstractSkipTablesPredicate {

    private SkipTablesByNamePredicate(@Nonnull final PgContext pgContext, @Nonnull final Collection<String> rawTableNamesToSkip) {
        super(pgContext, rawTableNamesToSkip);
    }

    private SkipTablesByNamePredicate(@Nonnull final PgContext pgContext, @Nonnull final String rawTableNameToSkip) {
        this(pgContext, prepareSingleNameToSkip(rawTableNameToSkip, "rawTableNameToSkip"));
    }

    /**
     * Creates a predicate to skip a specific table in the "public" schema.
     *
     * @param rawTableNameToSkip the name of the table to skip, must be non-null and non-blank
     * @return a predicate that skips the specified table in the "public" schema
     * @throws NullPointerException     if {@code tableName} is null
     * @throws IllegalArgumentException if {@code tableName} is blank
     */
    public static Predicate<DbObject> ofName(@Nonnull final String rawTableNameToSkip) {
        return new SkipTablesByNamePredicate(PgContext.ofPublic(), rawTableNameToSkip);
    }

    /**
     * Creates a predicate to skip specific tables in the "public" schema.
     *
     * @param rawTableNamesToSkip a collection of table names to skip, must be non-null
     * @return a predicate that skips the specified tables in the "public" schema
     * @throws NullPointerException if {@code rawTableNamesToSkip} is null
     */
    public static Predicate<DbObject> ofPublic(@Nonnull final Collection<String> rawTableNamesToSkip) {
        return new SkipTablesByNamePredicate(PgContext.ofPublic(), rawTableNamesToSkip);
    }

    /**
     * Creates a predicate to skip a specific table in the specified schema context.
     *
     * @param pgContext          the schema context for the table to be skipped, must be non-null
     * @param rawTableNameToSkip the name of the table to skip, must be non-null and non-blank
     * @return a predicate that skips the specified table in the given schema
     * @throws NullPointerException     if {@code pgContext} or {@code tableName} is null
     * @throws IllegalArgumentException if {@code tableName} is blank
     */
    public static Predicate<DbObject> ofName(@Nonnull final PgContext pgContext, @Nonnull final String rawTableNameToSkip) {
        return new SkipTablesByNamePredicate(pgContext, rawTableNameToSkip);
    }

    /**
     * Creates a predicate to skip specific tables in the specified schema context.
     *
     * @param pgContext           the schema context for the tables to be skipped, must be non-null
     * @param rawTableNamesToSkip a collection of table names to skip, must be non-null
     * @return a predicate that skips the specified tables in the given schema
     * @throws NullPointerException if {@code pgContext} or {@code rawTableNamesToSkip} is null
     */
    public static Predicate<DbObject> of(@Nonnull final PgContext pgContext, @Nonnull final Collection<String> rawTableNamesToSkip) {
        return new SkipTablesByNamePredicate(pgContext, rawTableNamesToSkip);
    }
}
