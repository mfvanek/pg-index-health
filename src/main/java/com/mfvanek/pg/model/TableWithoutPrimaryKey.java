package com.mfvanek.pg.model;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Таблицы без первичных ключей - это потенциальный источник bloat'а.
 * При этом pg_repack их пережать не сможет.
 */
public class TableWithoutPrimaryKey {

    private final String tableName;

    public TableWithoutPrimaryKey(String tableName) {
        this.tableName = Objects.requireNonNull(tableName);
    }

    @Nonnull
    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return TableWithoutPrimaryKey.class.getSimpleName() + "{" +
                "tableName=" + tableName +
                "}";
    }
}
