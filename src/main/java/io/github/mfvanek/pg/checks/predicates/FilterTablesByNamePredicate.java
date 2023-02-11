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

import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.utils.Locales;

import java.util.Collection;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Allows filter tables by their name.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class FilterTablesByNamePredicate extends AbstractFilterByName implements Predicate<TableNameAware> {

    private FilterTablesByNamePredicate(@Nonnull final Collection<String> exclusions) {
        super(exclusions);
    }

    private FilterTablesByNamePredicate(@Nonnull final String tableName) {
        super(tableName);
    }

    @Override
    public boolean test(@Nonnull final TableNameAware tableNameAware) {
        if (exclusions.isEmpty()) {
            return true;
        }
        return !exclusions.contains(tableNameAware.getTableName().toLowerCase(Locales.DEFAULT));
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
