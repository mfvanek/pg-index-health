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

import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.utils.Locales;

import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * @author Ivan Vakhrushev
 * @since 0.5.1
 */
public class FilterTablesByNamePredicate implements Predicate<TableNameAware> {

    private final Set<String> exclusions;

    public FilterTablesByNamePredicate(@Nonnull final Set<String> exclusions) {
        this.exclusions = Objects.requireNonNull(exclusions, "exclusions cannot be null");
    }

    @Override
    public boolean test(@Nonnull final TableNameAware tableNameAware) {
        if (exclusions.isEmpty()) {
            return true;
        }
        return !exclusions.contains(tableNameAware.getTableName().toLowerCase(Locales.DEFAULT));
    }
}
