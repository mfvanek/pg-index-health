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

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexNameAware;
import io.github.mfvanek.pg.model.index.IndexesAware;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import static io.github.mfvanek.pg.model.predicates.AbstractSkipTablesPredicate.prepareFullyQualifiedNamesToSkip;

/**
 * A predicate that skips specified indexes by name in database objects implementing the {@link DbObject} interface.
 * This class is immutable and thread-safe.
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
@Immutable
@ThreadSafe
public final class SkipIndexesByNamePredicate implements Predicate<DbObject> {

    private final Set<String> fullyQualifiedIndexNamesToSkip;

    private SkipIndexesByNamePredicate(@Nonnull final PgContext pgContext, @Nonnull final Collection<String> rawIndexNamesToSkip) {
        this.fullyQualifiedIndexNamesToSkip = prepareFullyQualifiedNamesToSkip(pgContext, rawIndexNamesToSkip);
    }

    private SkipIndexesByNamePredicate(@Nonnull final PgContext pgContext, @Nonnull final String rawIndexNameToSkip) {
        this(pgContext, AbstractSkipTablesPredicate.prepareSingleNameToSkip(rawIndexNameToSkip, "rawIndexNameToSkip"));
    }

    /**
     * Tests whether the specified {@code DbObject} should be skipped based on its index name.
     *
     * @param dbObject the database object to test; must be non-null
     * @return {@code true} if the {@code dbObject}'s index name does not match any of the names to skip; {@code false} otherwise
     */
    @Override
    public boolean test(@Nonnull final DbObject dbObject) {
        if (fullyQualifiedIndexNamesToSkip.isEmpty()) {
            return true;
        }
        if (dbObject instanceof IndexNameAware) {
            final IndexNameAware i = (IndexNameAware) dbObject;
            return !fullyQualifiedIndexNamesToSkip.contains(i.getIndexName().toLowerCase(Locale.ROOT));
        }
        if (dbObject instanceof IndexesAware) {
            final IndexesAware i = (IndexesAware) dbObject;
            for (final Index index : i.getIndexes()) {
                if (fullyQualifiedIndexNamesToSkip.contains(index.getIndexName().toLowerCase(Locale.ROOT))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates a predicate to skip a single index name in the public schema.
     *
     * @param rawIndexNameToSkip the raw index name to skip; must be non-null and non-blank
     * @return a {@link Predicate} to skip the specified index name
     */
    public static Predicate<DbObject> ofName(@Nonnull final String rawIndexNameToSkip) {
        return new SkipIndexesByNamePredicate(PgContext.ofPublic(), rawIndexNameToSkip);
    }

    /**
     * Creates a predicate to skip a collection of index names in the public schema.
     *
     * @param rawIndexNamesToSkip a collection of raw index names to skip; must be non-null
     * @return a {@link Predicate} to skip the specified index names
     */
    public static Predicate<DbObject> ofPublic(@Nonnull final Collection<String> rawIndexNamesToSkip) {
        return new SkipIndexesByNamePredicate(PgContext.ofPublic(), rawIndexNamesToSkip);
    }

    /**
     * Creates a predicate to skip a single index name in a specified schema context.
     *
     * @param pgContext          the PostgreSQL context used to enrich the raw index name with schema information; must be non-null
     * @param rawIndexNameToSkip the raw index name to skip; must be non-null and non-blank
     * @return a {@link Predicate} to skip the specified index name
     */
    public static Predicate<DbObject> ofName(@Nonnull final PgContext pgContext, @Nonnull final String rawIndexNameToSkip) {
        return new SkipIndexesByNamePredicate(pgContext, rawIndexNameToSkip);
    }

    /**
     * Creates a predicate to skip a collection of index names in a specified schema context.
     *
     * @param pgContext           the PostgreSQL context used to enrich each raw index name with schema information; must be non-null
     * @param rawIndexNamesToSkip a collection of raw index names to skip; must be non-null
     * @return a {@link Predicate} to skip the specified index names
     */
    public static Predicate<DbObject> of(@Nonnull final PgContext pgContext, @Nonnull final Collection<String> rawIndexNamesToSkip) {
        return new SkipIndexesByNamePredicate(pgContext, rawIndexNamesToSkip);
    }
}
