/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.model;

/**
 * Allows to get table size in bytes.
 * Used as a marker interface for filtering exclusions in
 * {@link io.github.mfvanek.pg.index.health.logger.AbstractIndexesHealthLogger}
 *
 * @author Ivan Vakhrushev
 * @see IndexSizeAware
 * @see io.github.mfvanek.pg.index.health.logger.Exclusions
 */
public interface TableSizeAware {

    /**
     * Gets table size in bytes.
     *
     * @return table size in bytes
     */
    long getTableSizeInBytes();
}
