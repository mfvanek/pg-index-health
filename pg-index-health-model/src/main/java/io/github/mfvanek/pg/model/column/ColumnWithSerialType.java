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

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.sequence.SequenceNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * An immutable representation of column with serial type in database table.
 *
 * @author Ivan Vakhrushev
 * @see SerialType
 * @see Column
 * @since 0.6.2
 */
public final class ColumnWithSerialType implements DbObject, ColumnNameAware, SequenceNameAware, Comparable<ColumnWithSerialType> {

    private final Column column;
    private final SerialType serialType;
    private final String sequenceName;

    private ColumnWithSerialType(final Column column,
                                 final SerialType serialType,
                                 final String sequenceName) {
        this.column = Objects.requireNonNull(column, "column cannot be null");
        this.serialType = Objects.requireNonNull(serialType, "serialType cannot be null");
        this.sequenceName = Validators.notBlank(sequenceName, "sequenceName");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return column.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgObjectType getObjectType() {
        return column.getObjectType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableName() {
        return column.getTableName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName() {
        return column.getColumnName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNotNull() {
        return column.isNotNull();
    }

    /**
     * Retrieves raw type of serial column.
     *
     * @return type of serial column
     */
    public SerialType getSerialType() {
        return serialType;
    }

    /**
     * Retrieves name of the associated sequence.
     *
     * @return name of the associated sequence
     */
    @Override
    public String getSequenceName() {
        return sequenceName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ColumnWithSerialType.class.getSimpleName() + "{column=" + column +
            ", serialType=" + serialType +
            ", sequenceName='" + sequenceName + '\'' +
            '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ColumnWithSerialType)) {
            return false;
        }

        final ColumnWithSerialType that = (ColumnWithSerialType) other;
        return Objects.equals(column, that.column) &&
            Objects.equals(serialType, that.serialType) &&
            Objects.equals(sequenceName, that.sequenceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(column, serialType, sequenceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final ColumnWithSerialType other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (!column.equals(other.column)) {
            return column.compareTo(other.column);
        }
        if (serialType != other.serialType) {
            return serialType.compareTo(other.serialType);
        }
        return sequenceName.compareTo(other.sequenceName);
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object of given serial type.
     *
     * @param column       column; should be non-null.
     * @param serialType   column serial type; should be non-null.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     */
    public static ColumnWithSerialType of(final Column column,
                                          final SerialType serialType,
                                          final String sequenceName) {
        return new ColumnWithSerialType(column, serialType, sequenceName);
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object of given serial type and context.
     *
     * @param pgContext    the schema context to enrich table name; must be non-null.
     * @param column       column; should be non-null.
     * @param serialType   column serial type; should be non-null.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     * @since 0.14.6
     */
    public static ColumnWithSerialType of(final PgContext pgContext,
                                          final Column column,
                                          final SerialType serialType,
                                          final String sequenceName) {
        return of(column, serialType, PgContext.enrichWith(sequenceName, pgContext));
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object of {@code bigserial} type.
     *
     * @param column       column; should be non-null.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     */
    public static ColumnWithSerialType ofBigSerial(final Column column,
                                                   final String sequenceName) {
        return of(column, SerialType.BIG_SERIAL, sequenceName);
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object of {@code bigserial} type and context.
     *
     * @param pgContext    the schema context to enrich table name; must be non-null.
     * @param column       column; should be non-null.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     * @since 0.15.0
     */
    public static ColumnWithSerialType ofBigSerial(final PgContext pgContext,
                                                   final Column column,
                                                   final String sequenceName) {
        return ofBigSerial(column, PgContext.enrichWith(sequenceName, pgContext));
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object of {@code serial} type.
     *
     * @param column       column; should be non-null.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     */
    public static ColumnWithSerialType ofSerial(final Column column,
                                                final String sequenceName) {
        return of(column, SerialType.SERIAL, sequenceName);
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object of {@code serial} type.
     *
     * @param pgContext    the schema context to enrich table name; must be non-null.
     * @param column       column; should be non-null.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     * @since 0.15.0
     */
    public static ColumnWithSerialType ofSerial(final PgContext pgContext,
                                                final Column column,
                                                final String sequenceName) {
        return ofSerial(column, PgContext.enrichWith(sequenceName, pgContext));
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object of {@code smallserial} type.
     *
     * @param column       column; should be non-null.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     */
    public static ColumnWithSerialType ofSmallSerial(final Column column,
                                                     final String sequenceName) {
        return of(column, SerialType.SMALL_SERIAL, sequenceName);
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object of {@code smallserial} type.
     *
     * @param pgContext    the schema context to enrich table name; must be non-null.
     * @param column       column; should be non-null.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     * @since 0.15.0
     */
    public static ColumnWithSerialType ofSmallSerial(final PgContext pgContext,
                                                     final Column column,
                                                     final String sequenceName) {
        return ofSmallSerial(column, PgContext.enrichWith(sequenceName, pgContext));
    }
}
