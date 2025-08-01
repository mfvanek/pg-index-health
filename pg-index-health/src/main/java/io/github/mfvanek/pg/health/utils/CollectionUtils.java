/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.utils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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
    public static <T> Collection<T> intersection(final Collection<? extends T> first, final Collection<? extends T> second) {
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
