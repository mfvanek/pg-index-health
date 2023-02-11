/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import io.github.mfvanek.pg.model.table.TableNameAware;

import javax.annotation.Nonnull;

/**
 * Allows getting column name.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.2
 * @see io.github.mfvanek.pg.model.table.TableNameAware
 */
public interface ColumnNameAware extends TableNameAware {

    /**
     * Gets column name in the table.
     *
     * @return column name
     */
    @Nonnull
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
