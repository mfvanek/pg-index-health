/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

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

    @Override
    @Nonnull
    public String getTableName() {
        return tableName;
    }

    @Override
    public long getTableSizeInBytes() {
        return tableSizeInBytes;
    }

    @SuppressWarnings("WeakerAccess")
    protected String innerToString() {
        return "tableName=\'" + tableName + "\'" +
                ", tableSizeInBytes=" + tableSizeInBytes;
    }

    @Override
    public String toString() {
        return Table.class.getSimpleName() + '{' + innerToString() + '}';
    }

    /**
     * Constructs a {@code Table} object.
     *
     * @param tableName        table name; should be non blank.
     * @param tableSizeInBytes table size in bytes; should be positive or zero.
     * @return {@code Table}
     */
    public static Table of(@Nonnull final String tableName, final long tableSizeInBytes) {
        return new Table(tableName, tableSizeInBytes);
    }
}
