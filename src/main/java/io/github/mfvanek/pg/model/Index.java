/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Index implements TableNameAware, IndexNameAware {

    private final String tableName;
    private final String indexName;

    @SuppressWarnings("WeakerAccess")
    protected Index(@Nonnull String tableName, @Nonnull String indexName) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.indexName = Validators.indexNameNotBlank(indexName);
    }

    @Override
    @Nonnull
    public String getTableName() {
        return tableName;
    }

    @Override
    @Nonnull
    public String getIndexName() {
        return indexName;
    }

    @Override
    public String toString() {
        return Index.class.getSimpleName() + '{' + innerToString() + '}';
    }

    @SuppressWarnings("WeakerAccess")
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
