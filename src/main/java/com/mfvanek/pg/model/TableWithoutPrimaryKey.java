/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import com.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

/**
 * Tables without primary keys can be a huge problem when bloat occurs because pg_repack will not be able to process them.
 */
public class TableWithoutPrimaryKey implements TableNameAware {

    private final String tableName;

    private TableWithoutPrimaryKey(@Nonnull final String tableName) {
        this.tableName = Validators.tableNameNotBlank(tableName);
    }

    @Override
    @Nonnull
    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return TableWithoutPrimaryKey.class.getSimpleName() + "{" +
                "tableName=\'" + tableName + "\'" +
                "}";
    }

    public static TableWithoutPrimaryKey of(@Nonnull final String tableName) {
        return new TableWithoutPrimaryKey(tableName);
    }
}
