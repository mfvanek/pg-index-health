/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.model.index.IndexNameAware;
import io.github.mfvanek.pg.utils.Locales;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * @author Ivan Vakhrushev
 * @since 0.5.1
 */
public class FilterIndexesByNamePredicate implements Predicate<IndexNameAware> {

    private final Set<String> indexesExclusions;

    public FilterIndexesByNamePredicate(@Nonnull final Set<String> indexesExclusions) {
        this.indexesExclusions = Objects.requireNonNull(indexesExclusions, "indexesExclusions cannot be null");
    }

    @Override
    public boolean test(@Nonnull final IndexNameAware indexNameAware) {
        if (indexesExclusions.isEmpty()) {
            return true;
        }
        return !indexesExclusions.contains(indexNameAware.getIndexName().toLowerCase(Locales.DEFAULT));
    }
}
