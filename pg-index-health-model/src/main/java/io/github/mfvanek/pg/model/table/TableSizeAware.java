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

import io.github.mfvanek.pg.model.index.IndexSizeAware;

/**
 * Allows getting table size in bytes.
 *
 * @author Ivan Vakhrushev
 * @see IndexSizeAware
 */
public interface TableSizeAware extends TableNameAware {

    /**
     * Gets table size in bytes.
     *
     * @return table size in bytes
     */
    long getTableSizeInBytes();
}
