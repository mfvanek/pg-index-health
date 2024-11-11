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
import io.github.mfvanek.pg.model.sequence.SequenceNameAware;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

import static io.github.mfvanek.pg.model.predicates.AbstractSkipTablesPredicate.prepareFullyQualifiedNamesToSkip;

/**
 * A predicate that filters out database objects based on a specified set of sequence names.
 *
 * @author Ivan Vakhrushev
 * @see Predicate
 * @see SequenceNameAware
 * @since 0.13.3
 */
@Immutable
@ThreadSafe
public final class SkipBySequenceNamePredicate implements Predicate<DbObject> {

    private final Set<String> fullyQualifiedSequenceNamesToSkip;

    private SkipBySequenceNamePredicate(@Nonnull final PgContext pgContext, @Nonnull final Collection<String> rawSequenceNamesToSkip) {
        this.fullyQualifiedSequenceNamesToSkip = prepareFullyQualifiedNamesToSkip(pgContext, rawSequenceNamesToSkip);
    }

    private SkipBySequenceNamePredicate(@Nonnull final PgContext pgContext, @Nonnull final String rawSequenceNameToSkip) {
        this(pgContext, AbstractSkipTablesPredicate.prepareSingleNameToSkip(rawSequenceNameToSkip, "rawSequenceNameToSkip"));
    }

    /**
     * Tests whether the given database object should be skipped based on its sequence name.
     *
     * @param dbObject the database object to test; must be non-null
     * @return {@code false} if the object's sequence name matches one in the skip list; {@code true} otherwise
     */
    @Override
    public boolean test(@Nonnull final DbObject dbObject) {
        if (!fullyQualifiedSequenceNamesToSkip.isEmpty() && dbObject instanceof SequenceNameAware) {
            final SequenceNameAware s = (SequenceNameAware) dbObject;
            return !fullyQualifiedSequenceNamesToSkip.contains(s.getSequenceName().toLowerCase(Locale.ROOT));
        }
        return true;
    }

    /**
     * Creates a predicate to skip a specific sequence name in the public context.
     *
     * @param rawSequenceNameToSkip the raw sequence name to skip; must be non-null and non-blank
     * @return a {@code SkipBySequenceNamePredicate} instance for the specified sequence name
     */
    public static Predicate<DbObject> ofName(@Nonnull final String rawSequenceNameToSkip) {
        return new SkipBySequenceNamePredicate(PgContext.ofPublic(), rawSequenceNameToSkip);
    }

    /**
     * Creates a predicate to skip a collection of sequence names in the public context.
     *
     * @param rawSequenceNamesToSkip the collection of raw sequence names to skip; must be non-null
     * @return a {@code SkipBySequenceNamePredicate} instance for the specified sequence names
     */
    public static Predicate<DbObject> of(@Nonnull final Collection<String> rawSequenceNamesToSkip) {
        return new SkipBySequenceNamePredicate(PgContext.ofPublic(), rawSequenceNamesToSkip);
    }

    /**
     * Creates a predicate to skip a specific sequence name in the given PostgreSQL context.
     *
     * @param pgContext             the PostgreSQL context to use; must be non-null
     * @param rawSequenceNameToSkip the raw sequence name to skip; must be non-null and non-blank
     * @return a {@code SkipBySequenceNamePredicate} instance for the specified sequence name
     */
    public static Predicate<DbObject> ofName(@Nonnull final PgContext pgContext, @Nonnull final String rawSequenceNameToSkip) {
        return new SkipBySequenceNamePredicate(pgContext, rawSequenceNameToSkip);
    }

    /**
     * Creates a predicate to skip a collection of sequence names in the given PostgreSQL context.
     *
     * @param pgContext              the PostgreSQL context to use; must be non-null
     * @param rawSequenceNamesToSkip the collection of raw sequence names to skip; must be non-null
     * @return a {@code SkipBySequenceNamePredicate} instance for the specified sequence names
     */
    public static Predicate<DbObject> of(@Nonnull final PgContext pgContext, @Nonnull final Collection<String> rawSequenceNamesToSkip) {
        return new SkipBySequenceNamePredicate(pgContext, rawSequenceNamesToSkip);
    }
}
