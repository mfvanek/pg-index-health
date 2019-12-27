/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
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
