/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

/**
 * Represents database table with information about bloat.
 *
 * @author Ivan Vakhrushev
 */
public class TableWithBloat extends Table implements BloatAware {

    private long bloatSizeInBytes;
    private int bloatPercentage;

    private TableWithBloat(@Nonnull final String tableName,
                           final long tableSizeInBytes,
                           long bloatSizeInBytes,
                           int bloatPercentage) {
        super(tableName, tableSizeInBytes);
        this.bloatSizeInBytes = Validators.sizeNotNegative(bloatSizeInBytes, "bloatSizeInBytes");
        this.bloatPercentage = Validators.argumentNotNegative(bloatPercentage, "bloatPercentage");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBloatSizeInBytes() {
        return bloatSizeInBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBloatPercentage() {
        return bloatPercentage;
    }

    @Override
    protected String innerToString() {
        return super.innerToString() + ", bloatSizeInBytes=" + bloatSizeInBytes +
                ", bloatPercentage=" + bloatPercentage;
    }

    @Override
    public String toString() {
        return TableWithBloat.class.getSimpleName() + '{' + innerToString() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Constructs a {@code TableWithBloat} object.
     *
     * @param tableName        table name; should be non blank.
     * @param tableSizeInBytes table size in bytes; should be positive or zero.
     * @param bloatSizeInBytes bloat amount in bytes; should be positive or zero.
     * @param bloatPercentage  bloat percentage in the range from 0 to 100 inclusive.
     * @return {@code TableWithBloat}
     */
    public static TableWithBloat of(@Nonnull final String tableName,
                                    final long tableSizeInBytes,
                                    long bloatSizeInBytes,
                                    int bloatPercentage) {
        return new TableWithBloat(tableName, tableSizeInBytes, bloatSizeInBytes, bloatPercentage);
    }
}
