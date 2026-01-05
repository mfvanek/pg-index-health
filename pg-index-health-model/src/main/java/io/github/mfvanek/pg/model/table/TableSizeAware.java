/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
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
     * Represents the field name used for retrieving the size of a table in bytes.
     */
    String TABLE_SIZE_IN_BYTES_FIELD = "tableSizeInBytes";
    /**
     * The name of the field that defines a table.
     */
    String TABLE_FIELD = "table";

    /**
     * Retrieves table size in bytes.
     *
     * @return table size in bytes
     */
    long getTableSizeInBytes();
}
