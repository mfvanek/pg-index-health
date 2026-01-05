/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Base class for all migration generators.
 *
 * @param <T> represents an object in a database associated with a table
 * @author Ivan Vakhrushev
 * @since 0.6.2
 */
abstract class AbstractDbMigrationGenerator<T extends TableNameAware> implements DbMigrationGenerator<T> {

    /**
     * The delimiter used in the generation of migration scripts.
     */
    protected static final String DELIMITER = "_";
    /**
     * The length of the delimiter.
     */
    protected static final int DELIMITER_LENGTH = DELIMITER.length();

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<String> generate(final List<T> rows) {
        Objects.requireNonNull(rows, "rows cannot be null");

        final Set<String> migrations = new LinkedHashSet<>(rows.size());
        for (final T row : rows) {
            migrations.add(generate(row));
        }
        return List.copyOf(migrations);
    }

    /**
     * Generates a migration script for a single row.
     * This method must be implemented by subclasses to provide the actual generation logic for a single row.
     *
     * @param row the row from which to generate a migration script, must not be null
     * @return the generated migration script
     */
    protected abstract String generate(T row);
}
