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

/**
 * Allows getting table name.
 *
 * @author Ivan Vakhrushev
 * @see io.github.mfvanek.pg.model.index.IndexNameAware
 */
public interface TableNameAware {

    /**
     * Represents the name of the table field as a constant string.
     * Can be used as an identifier or reference for table name fields.
     */
    String TABLE_NAME_FIELD = "tableName";

    /**
     * Retrieves table name.
     *
     * @return table name
     */
    String getTableName();
}
