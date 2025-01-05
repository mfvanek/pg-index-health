/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.table.TableSizeAware;

/**
 * Allows getting index size in bytes.
 *
 * @author Ivan Vakhrushev
 * @see TableSizeAware
 */
public interface IndexSizeAware extends IndexNameAware {

    /**
     * Retrieves index size in bytes.
     *
     * @return index size in bytes
     */
    long getIndexSizeInBytes();
}
