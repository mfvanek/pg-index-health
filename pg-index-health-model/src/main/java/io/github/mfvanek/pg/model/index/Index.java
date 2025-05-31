/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * An immutable representation of database index.
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 * @see IndexSizeAware
 */
public final class Index implements DbObject, TableNameAware, IndexSizeAware, Comparable<Index> {

    private final String tableName;
    private final String indexName;
    private final long indexSizeInBytes;

    /**
     * Constructs an {@code Index} object with the specified table name, index name, and index size.
     *
     * @param tableName        the name of the table associated with this index; must be non-blank.
     * @param indexName        the name of this index; must be non-blank.
     * @param indexSizeInBytes size of the index in bytes; must be non-negative.
     */
    private Index(final String tableName, final String indexName, final long indexSizeInBytes) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.indexName = Validators.indexNameNotBlank(indexName);
        this.indexSizeInBytes = Validators.sizeNotNegative(indexSizeInBytes, "indexSizeInBytes");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getIndexName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgObjectType getObjectType() {
        return PgObjectType.INDEX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableName() {
        return tableName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIndexName() {
        return indexName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getIndexSizeInBytes() {
        return indexSizeInBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Index.class.getSimpleName() + '{' + innerToString() + '}';
    }

    /**
     * An auxiliary utility method for implementing {@code toString()} in child classes.
     *
     * @return string representation of the internal fields of this class
     */
    String innerToString() {
        return "tableName='" + tableName + '\'' +
            ", indexName='" + indexName + '\'' +
            ", indexSizeInBytes=" + indexSizeInBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
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
    public int hashCode() {
        return Objects.hash(tableName, indexName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Index other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (!tableName.equals(other.tableName)) {
            return tableName.compareTo(other.tableName);
        }
        return indexName.compareTo(other.indexName);
    }

    /**
     * Constructs an {@code Index} object.
     *
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @return {@code Index}
     * @since 0.15.0
     */
    public static Index of(final String tableName,
                           final String indexName,
                           final long indexSizeInBytes) {
        return new Index(tableName, indexName, indexSizeInBytes);
    }

    /**
     * Constructs an {@code Index} object with given context.
     *
     * @param pgContext        the schema context to enrich table and index name; must be non-null.
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @return {@code Index}
     * @since 0.15.0
     */
    public static Index of(final PgContext pgContext,
                           final String tableName,
                           final String indexName,
                           final long indexSizeInBytes) {
        return of(PgContext.enrichWith(tableName, pgContext), PgContext.enrichWith(indexName, pgContext), indexSizeInBytes);
    }

    /**
     * Constructs an {@code Index} object with zero size.
     *
     * @param tableName table name; should be non-blank.
     * @param indexName index name; should be non-blank.
     * @return {@code Index}
     */
    public static Index of(final String tableName,
                           final String indexName) {
        return of(tableName, indexName, 0L);
    }

    /**
     * Constructs an {@code Index} object with zero size and given context.
     *
     * @param pgContext the schema context to enrich table and index name; must be non-null.
     * @param tableName table name; should be non-blank.
     * @param indexName index name; should be non-blank.
     * @return {@code Index}
     * @since 0.14.3
     */
    public static Index of(final PgContext pgContext,
                           final String tableName,
                           final String indexName) {
        return of(pgContext, tableName, indexName, 0L);
    }
}
