/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Utility class providing various collection-related utility methods.
 */
public final class CollectionUtils {

    private CollectionUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a {@link Collection} containing the intersection of the given {@link Collection}s.
     *
     * @param first  the first collection, cannot be null
     * @param second the second collection, cannot be null
     * @param <T>    the generic type that is able to represent the types contained in both input collections.
     * @return the intersection of the two collections
     */
    @Nonnull
    public static <T> Collection<T> intersection(@Nonnull final Collection<? extends T> first, @Nonnull final Collection<? extends T> second) {
        Objects.requireNonNull(first, "first cannot be null");
        final Set<T> prepared = Set.copyOf(Objects.requireNonNull(second, "second cannot be null"));
        final Set<T> result = new LinkedHashSet<>();
        for (final T item : first) {
            if (prepared.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }
}
