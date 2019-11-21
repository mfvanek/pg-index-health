/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Index implements TableAware {

    private final String tableName;
    private final String indexName;

    protected Index(@Nonnull String tableName, @Nonnull String indexName) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.indexName = Validators.indexNameNotBlank(indexName);
    }

    @Override
    @Nonnull
    public String getTableName() {
        return tableName;
    }

    @Nonnull
    public String getIndexName() {
        return indexName;
    }

    @Override
    public String toString() {
        return Index.class.getSimpleName() + "{" +
                innerToString() +
                "}";
    }

    protected String innerToString() {
        return "tableName=\'" + tableName + "\'" +
                ", indexName=\'" + indexName + "\'";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Index that = (Index) o;
        return tableName.equals(that.tableName) &&
                indexName.equals(that.indexName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName, indexName);
    }

    public static Index of(@Nonnull String tableName, @Nonnull String indexName) {
        return new Index(tableName, indexName);
    }
}
