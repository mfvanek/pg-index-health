/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.object.PgObjectType;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A base representation of database index.
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 * @see IndexNameAware
 */
@Immutable
public class Index implements DbObject, TableNameAware, IndexNameAware, Comparable<Index> {

    private final String tableName;
    private final String indexName;

    @SuppressWarnings("WeakerAccess")
    protected Index(@Nonnull final String tableName, @Nonnull final String indexName) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.indexName = Validators.indexNameNotBlank(indexName);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final String getName() {
        return getIndexName();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public PgObjectType getObjectType() {
        return PgObjectType.INDEX;
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
    @Nonnull
    public String getIndexName() {
        return indexName;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return Index.class.getSimpleName() + '{' + innerToString() + '}';
    }

    /**
     * An auxiliary utility method for implementing {@code toString()} in child classes.
     *
     * @return string representation of the internal fields of this class
     */
    @SuppressWarnings("WeakerAccess")
    @Nonnull
    protected String innerToString() {
        return "tableName='" + tableName + '\'' +
            ", indexName='" + indexName + '\'';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Index)) {
            return false;
        }

        final Index that = (Index) other;
        return Objects.equals(tableName, that.tableName) &&
            Objects.equals(indexName, that.indexName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hash(tableName, indexName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@Nonnull final Index other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (!tableName.equals(other.tableName)) {
            return tableName.compareTo(other.tableName);
        }
        return indexName.compareTo(other.indexName);
    }

    /**
     * Constructs an {@code Index} object.
     *
     * @param tableName table name; should be non-blank.
     * @param indexName index name; should be non-blank.
     * @return {@code Index}
     */
    public static Index of(@Nonnull final String tableName, @Nonnull final String indexName) {
        return new Index(tableName, indexName);
    }
}
