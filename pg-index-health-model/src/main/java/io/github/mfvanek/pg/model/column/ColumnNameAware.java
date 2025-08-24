/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import io.github.mfvanek.pg.model.table.TableNameAware;

/**
 * Allows getting column name.
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 * @since 0.6.2
 */
public interface ColumnNameAware extends TableNameAware {

    /**
     * Represents the name of the column field.
     */
    String COLUMN_NAME_FIELD = "columnName";

    /**
     * Represents a constant for identifying a "not null" column constraint.
     * It signifies that a column cannot contain null values.
     */
    String NOT_NULL_FIELD = "notNull";

    /**
     * Retrieves column name in the table.
     *
     * @return column name
     */
    String getColumnName();

    /**
     * Shows whether a column can or cannot accept null values.
     *
     * @return true if a column cannot accept null values
     */
    boolean isNotNull();

    /**
     * Shows whether a column can accept null values.
     *
     * @return true if a column can accept null values
     */
    default boolean isNullable() {
        return !isNotNull();
    }
}
