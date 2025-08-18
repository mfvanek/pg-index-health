/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
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
     * Represents the field name used for retrieving the size of an index in bytes.
     */
    String INDEX_SIZE_IN_BYTES_FIELD = "indexSizeInBytes";
    /**
     * The name of the field that defines an index.
     */
    String INDEX_FIELD = "index";

    /**
     * Retrieves index size in bytes.
     *
     * @return index size in bytes
     */
    long getIndexSizeInBytes();
}
