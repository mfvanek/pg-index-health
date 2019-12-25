/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

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

    public static Table of(@Nonnull final String tableName, final long tableSizeInBytes) {
        return new Table(tableName, tableSizeInBytes);
    }
}
