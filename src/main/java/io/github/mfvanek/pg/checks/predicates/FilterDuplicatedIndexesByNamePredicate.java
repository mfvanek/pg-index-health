/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.predicates;

import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.utils.Locales;

import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Allows filter duplicated indexes by their name.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class FilterDuplicatedIndexesByNamePredicate extends AbstractFilterByName implements Predicate<DuplicatedIndexes> {

    private FilterDuplicatedIndexesByNamePredicate(@Nonnull final Collection<String> exclusions) {
        super(exclusions);
    }

    private FilterDuplicatedIndexesByNamePredicate(@Nonnull final String indexName) {
        super(indexName);
    }

    @Override
    public boolean test(@Nonnull final DuplicatedIndexes duplicatedIndexes) {
        if (exclusions.isEmpty()) {
            return true;
        }
        return duplicatedIndexes.getIndexNames().stream()
                .map(n -> n.toLowerCase(Locales.DEFAULT))
                .noneMatch(exclusions::contains);
    }

    @Nonnull
    public static Predicate<DuplicatedIndexes> of(@Nonnull final Collection<String> exclusions) {
        return new FilterDuplicatedIndexesByNamePredicate(exclusions);
    }

    @Nonnull
    public static Predicate<DuplicatedIndexes> of(@Nonnull final String indexName) {
        return new FilterDuplicatedIndexesByNamePredicate(indexName);
    }
}
