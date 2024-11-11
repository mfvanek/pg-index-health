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
import io.github.mfvanek.pg.model.validation.Validators;

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
 * A predicate for filtering database objects by their fully qualified names.
 * <p>
 * This class allows creating a predicate that skips specific database objects by name, where the
 * names to skip can be provided as a single name or as a collection of names. The names are case-insensitive,
 * making this predicate reliable for databases with varying case conventions.
 * </p>
 *
 * @author Ivan Vakhrushev
 * @see Predicate
 * @see DbObject
 * @since 0.13.3
 */
@Immutable
@ThreadSafe
public final class SkipDbObjectsByNamePredicate implements Predicate<DbObject> {

    /**
     * Set of fully qualified names to skip, stored in lowercase for case-insensitive comparison.
     */
    private final Set<String> fullyQualifiedNamesToSkip;

    private SkipDbObjectsByNamePredicate(@Nonnull final Collection<String> fullyQualifiedObjectNamesToSkip) {
        this.fullyQualifiedNamesToSkip = Objects.requireNonNull(fullyQualifiedObjectNamesToSkip, "fullyQualifiedObjectNamesToSkip cannot be null")
            .stream()
            .map(s -> s.toLowerCase(Locale.ROOT))
            .collect(Collectors.toUnmodifiableSet());
    }

    private SkipDbObjectsByNamePredicate(@Nonnull final String fullyQualifiedObjectNameToSkip) {
        this(Set.of(Validators.notBlank(fullyQualifiedObjectNameToSkip, "fullyQualifiedObjectNameToSkip")));
    }

    /**
     * Tests whether the specified {@link DbObject} should be included based on its name.
     * <p>
     * If the name of the given {@code DbObject} matches any of the names to skip, this predicate returns {@code false}.
     * Otherwise, it returns {@code true}.
     * </p>
     *
     * @param objectNameAware the {@code DbObject} whose name will be checked
     * @return {@code true} if the object should not be skipped, {@code false} if it should be skipped
     */
    @Override
    public boolean test(@Nonnull final DbObject objectNameAware) {
        if (fullyQualifiedNamesToSkip.isEmpty()) {
            return true;
        }
        return !fullyQualifiedNamesToSkip.contains(objectNameAware.getName().toLowerCase(Locale.ROOT));
    }

    /**
     * Creates a predicate to skip a specific fully qualified object name.
     *
     * @param fullyQualifiedObjectNameToSkip the fully qualified object name to skip, must be non-null and non-blank
     * @return a predicate that skips the specified object
     * @throws NullPointerException     if {@code fullyQualifiedObjectNameToSkip} is null
     * @throws IllegalArgumentException if {@code fullyQualifiedObjectNameToSkip} is blank
     */
    @Nonnull
    public static Predicate<DbObject> ofName(@Nonnull final String fullyQualifiedObjectNameToSkip) {
        return new SkipDbObjectsByNamePredicate(fullyQualifiedObjectNameToSkip);
    }

    /**
     * Creates a predicate to skip multiple fully qualified object names.
     *
     * @param fullyQualifiedObjectNamesToSkip a collection of fully qualified object names to skip, must be non-null
     * @return a predicate that skips the specified objects
     * @throws NullPointerException if {@code fullyQualifiedObjectNamesToSkip} is null
     */
    @Nonnull
    public static Predicate<DbObject> of(@Nonnull final Collection<String> fullyQualifiedObjectNamesToSkip) {
        return new SkipDbObjectsByNamePredicate(fullyQualifiedObjectNamesToSkip);
    }
}
