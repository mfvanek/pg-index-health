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

import io.github.mfvanek.pg.model.context.PgContext;
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
@SuppressWarnings("checkstyle:EqualsHashCode")
public final class ColumnWithSerialType extends AbstractColumnAware implements SequenceNameAware, Comparable<ColumnWithSerialType> {

    /**
     * The field name representing the type of serial column in a database.
     */
    public static final String SERIAL_TYPE_FIELD = "serialType";

    private final SerialType serialType;
    private final String sequenceName;

    private ColumnWithSerialType(final Column column,
                                 final SerialType serialType,
                                 final String sequenceName) {
        super(column, Objects.requireNonNull(serialType, SERIAL_TYPE_FIELD + " cannot be null").getColumnType());
        this.serialType = serialType;
        this.sequenceName = Validators.notBlank(sequenceName, SEQUENCE_NAME_FIELD);
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
     * Retrieves the name of the associated sequence.
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
        return ColumnWithSerialType.class.getSimpleName() + '{' +
            innerToString() +
            ", " + SERIAL_TYPE_FIELD + '=' + serialType +
            ", " + SEQUENCE_NAME_FIELD + "='" + sequenceName + '\'' +
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

        if (!(other instanceof final ColumnWithSerialType that)) {
            return false;
        }

        return Objects.equals(column, that.column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(column);
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
     * Constructs a {@code ColumnWithSerialType} object of a given serial type.
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
     * Constructs a {@code ColumnWithSerialType} object of a given serial type and context.
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
