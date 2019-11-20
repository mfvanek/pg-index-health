/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import javax.annotation.Nonnull;

/**
 * Таблицы без первичных ключей - это потенциальный источник bloat'а.
 * При этом pg_repack их пережать не сможет.
 */
public class TableWithoutPrimaryKey implements TableAware {

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
