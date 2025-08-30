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

import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * Abstract base class for database objects that are associated with a specific column.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
abstract class AbstractColumnAware implements DbObject, ColumnTypeAware {

    protected final Column column;
    protected final String columnType;

    protected AbstractColumnAware(final Column column,
                                  final String columnType) {
        this.column = Objects.requireNonNull(column, COLUMN_FIELD + " cannot be null");
        this.columnType = Validators.notBlank(columnType, COLUMN_TYPE_FIELD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getName() {
        return column.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PgObjectType getObjectType() {
        return column.getObjectType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getTableName() {
        return column.getTableName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getColumnName() {
        return column.getColumnName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isNotNull() {
        return column.isNotNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getColumnType() {
        return columnType;
    }

    /**
     * Retrieves the current instance as a {@code Column}.
     *
     * @return the {@code Column} associated with this instance
     * @author Ivan Vakhrushev
     * @since 0.20.3
     */
    public final Column toColumn() {
        return column;
    }

    /**
     * An auxiliary utility method for implementing {@code toString()} in child classes.
     *
     * @return string representation of the internal fields of this class
     */
    protected String innerToString() {
        return COLUMN_FIELD + '=' + column +
            ", " + COLUMN_TYPE_FIELD + "='" + columnType + '\'';
    }
}
