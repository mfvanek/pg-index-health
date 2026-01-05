/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

/**
 * Allows getting column name and type.
 *
 * @author Ivan Vakhrushev
 * @see ColumnNameAware
 * @since 0.20.3
 */
public interface ColumnTypeAware extends ColumnNameAware {

    /**
     * Represents the constant value for identifying the "column" field.
     */
    String COLUMN_FIELD = "column";
    /**
     * The field name representing the type of column in a database.
     */
    String COLUMN_TYPE_FIELD = "columnType";

    /**
     * Retrieves column type in the table.
     *
     * @return column type
     */
    String getColumnType();
}
