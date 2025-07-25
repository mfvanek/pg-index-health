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
 * @see io.github.mfvanek.pg.model.table.TableNameAware
 * @since 0.6.2
 */
public interface ColumnNameAware extends TableNameAware {

    /**
     * Retrieves column name in the table.
     *
     * @return column name
     */
    String getColumnName();

    /**
     * Shows whether column can or cannot accept null values.
     *
     * @return true if column cannot accept null values
     */
    boolean isNotNull();

    /**
     * Shows whether column can accept null values.
     *
     * @return true if column can accept null values
     */
    default boolean isNullable() {
        return !isNotNull();
    }
}
