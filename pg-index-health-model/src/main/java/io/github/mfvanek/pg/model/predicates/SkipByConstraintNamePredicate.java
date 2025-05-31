/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.constraint.ConstraintNameAware;
import io.github.mfvanek.pg.model.constraint.ConstraintsAware;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A predicate that filters out database objects based on a specified set of constraint names.
 *
 * @author Ivan Vakhrushev
 * @see Predicate
 * @see ConstraintNameAware
 * @see ConstraintsAware
 * @since 0.15.0
 */
@Immutable
public final class SkipByConstraintNamePredicate implements Predicate<DbObject> {

    private final Set<String> constraintNamesToSkip;

    private SkipByConstraintNamePredicate(@Nonnull final Collection<String> constraintNamesToSkip) {
        this.constraintNamesToSkip = Objects.requireNonNull(constraintNamesToSkip, "constraintNamesToSkip cannot be null")
            .stream()
            .map(s -> s.toLowerCase(Locale.ROOT))
            .collect(Collectors.toUnmodifiableSet());
    }

    private SkipByConstraintNamePredicate(@Nonnull final String constraintNameToSkip) {
        this(AbstractSkipTablesPredicate.prepareSingleNameToSkip(constraintNameToSkip, "constraintNameToSkip"));
    }

    /**
     * Tests whether the given database object should be skipped based on its constraint name.
     *
     * @param dbObject the database object to test; must be non-null
     * @return {@code false} if the object's constraint name matches one in the skip list; {@code true} otherwise
     */
    @Override
    public boolean test(@Nonnull final DbObject dbObject) {
        if (constraintNamesToSkip.isEmpty()) {
            return true;
        }
        if (dbObject instanceof ConstraintNameAware) {
            final ConstraintNameAware c = (ConstraintNameAware) dbObject;
            return !constraintNamesToSkip.contains(c.getConstraintName().toLowerCase(Locale.ROOT));
        }
        if (dbObject instanceof ConstraintsAware) {
            final ConstraintsAware cs = (ConstraintsAware) dbObject;
            for (final ConstraintNameAware c : cs.getConstraints()) {
                if (constraintNamesToSkip.contains(c.getConstraintName().toLowerCase(Locale.ROOT))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates a predicate to skip a specific constraint name.
     *
     * @param constraintNameToSkip the constraint name to skip; must be non-blank
     * @return a {@code SkipByConstraintNamePredicate} instance for the specified constraint name
     */
    public static Predicate<DbObject> ofName(@Nonnull final String constraintNameToSkip) {
        return new SkipByConstraintNamePredicate(constraintNameToSkip);
    }

    /**
     * Creates a predicate to skip a collection of constraint names.
     *
     * @param constraintNamesToSkip the collection of constraint names to skip; must be non-null
     * @return a {@code SkipByConstraintNamePredicate} instance for the specified constraint names
     */
    public static Predicate<DbObject> of(@Nonnull final Collection<String> constraintNamesToSkip) {
        return new SkipByConstraintNamePredicate(constraintNamesToSkip);
    }
}
