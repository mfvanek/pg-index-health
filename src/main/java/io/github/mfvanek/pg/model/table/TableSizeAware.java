/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.common.health.logger.AbstractHealthLogger;
import io.github.mfvanek.pg.model.index.IndexSizeAware;

/**
 * Allows to get table size in bytes.
 * Used as a marker interface for filtering exclusions in
 * {@link AbstractHealthLogger}
 *
 * @author Ivan Vakhrushev
 * @see IndexSizeAware
 * @see io.github.mfvanek.pg.common.health.logger.Exclusions
 */
public interface TableSizeAware {

    /**
     * Gets table size in bytes.
     *
     * @return table size in bytes
     */
    long getTableSizeInBytes();
}
