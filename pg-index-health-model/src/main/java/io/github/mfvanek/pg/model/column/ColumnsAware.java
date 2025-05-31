/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Representing an object that is aware of a collection of {@link Column} instances (zero or more).
 *
 * @author Ivan Vakhrushev
 * @since 0.14.6
 */
public interface ColumnsAware {

    /**
     * Retrieves a list of {@link Column} instances associated with this object.
     *
     * @return a non-null list of columns
     */
    List<ColumnNameAware> getColumns();

    /**
     * Retrieves the first column in the list.
     *
     * @return the first column in the list if present; otherwise null
     * @since 0.15.0
     */
    @Nullable
    default ColumnNameAware getFirstColumn() {
        final List<ColumnNameAware> columns = getColumns();
        if (columns.isEmpty()) {
            return null;
        }
        return columns.get(0);
    }
}
