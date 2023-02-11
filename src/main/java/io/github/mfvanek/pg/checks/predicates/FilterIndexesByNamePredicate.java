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

import io.github.mfvanek.pg.model.index.IndexNameAware;
import io.github.mfvanek.pg.utils.Locales;

import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Allows filter indexes by their name.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class FilterIndexesByNamePredicate extends AbstractFilterByName implements Predicate<IndexNameAware> {

    private FilterIndexesByNamePredicate(@Nonnull final Collection<String> exclusions) {
        super(exclusions);
    }

    private FilterIndexesByNamePredicate(@Nonnull final String indexName) {
        super(indexName);
    }

    @Override
    public boolean test(@Nonnull final IndexNameAware indexNameAware) {
        if (exclusions.isEmpty()) {
            return true;
        }
        return !exclusions.contains(indexNameAware.getIndexName().toLowerCase(Locales.DEFAULT));
    }

    @Nonnull
    public static Predicate<IndexNameAware> of(@Nonnull final String indexName) {
        return new FilterIndexesByNamePredicate(indexName);
    }

    @Nonnull
    public static Predicate<IndexNameAware> of(@Nonnull final Collection<String> exclusions) {
        return new FilterIndexesByNamePredicate(exclusions);
    }
}
