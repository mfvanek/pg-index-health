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

import javax.annotation.Nonnull;

/**
 * Allows to get table name.
 * Used as a marker interface for filtering exclusions in
 * {@link io.github.mfvanek.pg.index.health.logger.AbstractIndexesHealthLogger}
 *
 * @author Ivan Vakhrushev
 * @see IndexNameAware
 * @see io.github.mfvanek.pg.index.health.logger.Exclusions
 */
public interface TableNameAware {

    /**
     * Gets table name.
     *
     * @return table name
     */
    @Nonnull
    String getTableName();
}
