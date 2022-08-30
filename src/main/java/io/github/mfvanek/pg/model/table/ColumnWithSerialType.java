/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.utils.Validators;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A representation of column with serial type in database table.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.2
 * @see SerialType
 */
@Immutable
public class ColumnWithSerialType implements ColumnNameAware, Comparable<ColumnWithSerialType> {

    private final Column column;
    private final SerialType serialType;
    private final String sequenceName;

    private ColumnWithSerialType(@Nonnull final Column column,
                                 @Nonnull final SerialType serialType,
                                 @Nonnull final String sequenceName) {
        this.column = Objects.requireNonNull(column, "column cannot be null");
        this.serialType = Objects.requireNonNull(serialType, "serialType cannot be null");
        this.sequenceName = Validators.notBlank(sequenceName, "sequenceName");
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getTableName() {
        return column.getTableName();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
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
     * Gets raw type of serial column.
     *
     * @return type of serial column
     */
    @Nonnull
    public SerialType getSerialType() {
        return serialType;
    }

    /**
     * Gets name of the associated sequence.
     *
     * @return name of the associated sequence
     */
    @Nonnull
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
    public final boolean equals(final Object other) {
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
    public final int hashCode() {
        return Objects.hash(column, serialType, sequenceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@Nonnull final ColumnWithSerialType other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (!column.equals(other.column)) {
            return column.compareTo(other.column);
        }
        if (!serialType.equals(other.serialType)) {
            return serialType.compareTo(other.serialType);
        }
        return sequenceName.compareTo(other.sequenceName);
    }

    @Nonnull
    public static ColumnWithSerialType of(@Nonnull final Column column,
                                          @Nonnull final SerialType serialType,
                                          @Nonnull final String sequenceName) {
        return new ColumnWithSerialType(column, serialType, sequenceName);
    }
}
