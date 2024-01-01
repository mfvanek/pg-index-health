/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Database migrations generator.
 *
 * @param <T> represents an object in a database associated with a table
 *
 * @author Ivan Vahrushev
 * @since 0.5.0
 */
public interface DbMigrationGenerator<T extends TableNameAware> {

    /**
     * Generates sql migrations based on the specified rows.
     *
     * @param rows a set of data on the basis of which the sql migration will be generated
     * @return a list of generated sql migrations
     * @since 0.10.0
     */
    @Nonnull
    List<String> generate(@Nonnull List<T> rows);
}
