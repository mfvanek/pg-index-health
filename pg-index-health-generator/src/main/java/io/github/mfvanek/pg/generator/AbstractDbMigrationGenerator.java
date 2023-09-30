/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Base class for all migration generators.
 *
 * @param <T> represents an object in a database associated with a table
 *
 * @author Ivan Vahrushev
 * @since 0.6.2
 */
abstract class AbstractDbMigrationGenerator<T extends TableNameAware> implements DbMigrationGenerator<T> {

    protected static final String DELIMITER = "_";

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final List<String> generate(@Nonnull final List<T> rows) {
        Objects.requireNonNull(rows, "rows cannot be null");

        final List<String> migrations = new ArrayList<>(rows.size());
        for (final T row : rows) {
            migrations.add(generate(row));
        }
        return migrations;
    }

    @Nonnull
    protected abstract String generate(@Nonnull T row);
}
