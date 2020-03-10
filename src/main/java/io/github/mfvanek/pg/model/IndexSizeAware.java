/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model;

/**
 * Allows to get index size in bytes.
 * Used as a marker interface for filtering exclusions in
 * {@link io.github.mfvanek.pg.index.health.logger.AbstractIndexesHealthLogger}
 *
 * @author Ivan Vakhrushev
 * @see TableSizeAware
 * @see io.github.mfvanek.pg.index.health.logger.Exclusions
 */
public interface IndexSizeAware {

    /**
     * Gets index size in bytes.
     *
     * @return index size in bytes
     */
    long getIndexSizeInBytes();
}
