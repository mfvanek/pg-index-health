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

import io.github.mfvanek.pg.model.table.TableSizeAware;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Allows filter tables by their size.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 * @see TableSizeAware
 */
public class FilterTablesBySizePredicate extends AbstractFilterBySize implements Predicate<TableSizeAware> {

    private FilterTablesBySizePredicate(final long thresholdInBytes) {
        super(thresholdInBytes);
    }

    @Override
    public boolean test(@Nonnull final TableSizeAware tableSizeAware) {
        return tableSizeAware.getTableSizeInBytes() >= thresholdInBytes;
    }

    @Nonnull
    public static Predicate<TableSizeAware> of(final long thresholdInBytes) {
        return new FilterTablesBySizePredicate(thresholdInBytes);
    }
}
