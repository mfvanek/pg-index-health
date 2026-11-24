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
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexNameAware;
import io.github.mfvanek.pg.model.index.IndexesAware;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

import static io.github.mfvanek.pg.model.predicates.AbstractSkipTablesPredicate.collectNamesToSkip;
import static io.github.mfvanek.pg.model.predicates.AbstractSkipTablesPredicate.prepareFullyQualifiedNamesToSkip;

/**
 * A predicate that skips specified indexes by name in database objects implementing the {@link DbObject} interface.
 * <p>
 * It can be configured with either a single index name or a collection of index names to skip. The names are enriched
 * with schema information, if available, to ensure they match the fully qualified index names in the database.
 * </p>
 *
 * @author Ivan Vakhrushev
 * @see DbObject
 * @see IndexNameAware
 * @see PgContext
 * @since 0.13.3
 */
public final class SkipIndexesByNamePredicate implements Predicate<DbObject> {

    private final Set<String> fullyQualifiedIndexNamesToSkip;

    private SkipIndexesByNamePredicate(final PgContext ctx, final Collection<String> rawIndexNamesToSkip) {
        this.fullyQualifiedIndexNamesToSkip = prepareFullyQualifiedNamesToSkip(ctx, rawIndexNamesToSkip);
    }

    private SkipIndexesByNamePredicate(final PgContext ctx, final String rawIndexNameToSkip, final String... rawIndexNamesToSkip) {
        this(ctx, collectNamesToSkip(rawIndexNameToSkip, rawIndexNamesToSkip, "rawIndexNameToSkip", "rawIndexNamesToSkip"));
    }

    /**
     * Tests whether the specified {@code DbObject} should be skipped based on its index name.
     *
     * @param dbObject the database object to test; must be non-null
     * @return {@code true} if the {@code dbObject}'s index name does not match any of the names to skip; {@code false} otherwise
     */
    @Override
    public boolean test(final DbObject dbObject) {
        if (fullyQualifiedIndexNamesToSkip.isEmpty()) {
            return true;
        }
        if (dbObject instanceof final IndexNameAware i) {
            return !fullyQualifiedIndexNamesToSkip.contains(i.getIndexName().toLowerCase(Locale.ROOT));
        }
        if (dbObject instanceof final IndexesAware is) {
            for (final Index index : is.getIndexes()) {
                if (fullyQualifiedIndexNamesToSkip.contains(index.getIndexName().toLowerCase(Locale.ROOT))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates a predicate to skip one or more index names in the default schema.
     *
     * @param rawIndexNameToSkip  the raw index name to skip; must be non-null and non-blank
     * @param rawIndexNamesToSkip other raw index names to skip; must be non-null and must not contain null or blank elements
     * @return a {@link Predicate} to skip the specified index names
     * @since 0.60.0
     */
    public static Predicate<DbObject> ofName(final String rawIndexNameToSkip, final String... rawIndexNamesToSkip) {
        return new SkipIndexesByNamePredicate(PgContext.ofDefault(), rawIndexNameToSkip, rawIndexNamesToSkip);
    }

    /**
     * Creates a predicate to skip a collection of index names in the default schema.
     *
     * @param rawIndexNamesToSkip a collection of raw index names to skip; must be non-null
     * @return a {@link Predicate} to skip the specified index names
     */
    public static Predicate<DbObject> ofDefault(final Collection<String> rawIndexNamesToSkip) {
        return new SkipIndexesByNamePredicate(PgContext.ofDefault(), rawIndexNamesToSkip);
    }

    /**
     * Creates a predicate to skip one or more index names in a specified schema context.
     *
     * @param ctx                 the PostgreSQL context used to enrich the raw index names with schema information; must be non-null
     * @param rawIndexNameToSkip  the raw index name to skip; must be non-null and non-blank
     * @param rawIndexNamesToSkip other raw index names to skip; must be non-null and must not contain null or blank elements
     * @return a {@link Predicate} to skip the specified index names
     * @since 0.60.0
     */
    public static Predicate<DbObject> ofName(final PgContext ctx, final String rawIndexNameToSkip, final String... rawIndexNamesToSkip) {
        return new SkipIndexesByNamePredicate(ctx, rawIndexNameToSkip, rawIndexNamesToSkip);
    }

    /**
     * Creates a predicate to skip a collection of index names in a specified schema context.
     *
     * @param ctx                 the PostgreSQL context used to enrich each raw index name with schema information; must be non-null
     * @param rawIndexNamesToSkip a collection of raw index names to skip; must be non-null
     * @return a {@link Predicate} to skip the specified index names
     */
    public static Predicate<DbObject> of(final PgContext ctx, final Collection<String> rawIndexNamesToSkip) {
        return new SkipIndexesByNamePredicate(ctx, rawIndexNamesToSkip);
    }
}
