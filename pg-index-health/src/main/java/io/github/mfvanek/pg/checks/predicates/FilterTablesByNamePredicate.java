/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.predicates;

import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.Collection;
import java.util.Locale;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Allows filter tables by their name.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 * @deprecated This class has been replaced by {@link io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate}
 */
@Deprecated(since = "0.13.3", forRemoval = true)
public class FilterTablesByNamePredicate extends AbstractFilterByName implements Predicate<TableNameAware> {

    private FilterTablesByNamePredicate(@Nonnull final Collection<String> exclusions) {
        super(exclusions);
    }

    private FilterTablesByNamePredicate(@Nonnull final String tableName) {
        super(tableName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(@Nonnull final TableNameAware tableNameAware) {
        if (exclusions.isEmpty()) {
            return true;
        }
        return !exclusions.contains(tableNameAware.getTableName().toLowerCase(Locale.ROOT));
    }

    @Nonnull
    public static Predicate<TableNameAware> of(@Nonnull final String tableName) {
        return new FilterTablesByNamePredicate(tableName);
    }

    @Nonnull
    public static Predicate<TableNameAware> of(@Nonnull final Collection<String> exclusions) {
        return new FilterTablesByNamePredicate(exclusions);
    }
}
