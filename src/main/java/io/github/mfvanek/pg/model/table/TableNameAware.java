/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import javax.annotation.Nonnull;

/**
 * Allows getting table name.
 * Used as a marker interface for filtering exclusions in
 * {@link io.github.mfvanek.pg.common.health.logger.AbstractHealthLogger}
 *
 * @author Ivan Vakhrushev
 * @see io.github.mfvanek.pg.model.index.IndexNameAware
 * @see io.github.mfvanek.pg.common.health.logger.Exclusions
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
