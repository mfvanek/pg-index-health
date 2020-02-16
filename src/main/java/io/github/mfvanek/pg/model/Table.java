/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A base representation of database table.
 *
 * @author Ivan Vakhrushev
 */
public class Table implements TableNameAware, TableSizeAware {

    private final String tableName;
    private final long tableSizeInBytes;

    @SuppressWarnings("WeakerAccess")
    protected Table(@Nonnull final String tableName, final long tableSizeInBytes) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.tableSizeInBytes = Validators.sizeNotNegative(tableSizeInBytes, "tableSizeInBytes");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getTableName() {
        return tableName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTableSizeInBytes() {
        return tableSizeInBytes;
    }

    @SuppressWarnings("WeakerAccess")
    protected String innerToString() {
        return "tableName='" + tableName + '\'' +
                ", tableSizeInBytes=" + tableSizeInBytes;
    }

    @Override
    public String toString() {
        return Table.class.getSimpleName() + '{' + innerToString() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Table that = (Table) o;
        return Objects.equals(tableName, that.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName);
    }

    /**
     * Constructs a {@code Table} object.
     *
     * @param tableName        table name; should be non blank.
     * @param tableSizeInBytes table size in bytes; should be positive or zero.
     * @return {@code Table}
     */
    @Nonnull
    public static Table of(@Nonnull final String tableName, final long tableSizeInBytes) {
        return new Table(tableName, tableSizeInBytes);
    }
}
