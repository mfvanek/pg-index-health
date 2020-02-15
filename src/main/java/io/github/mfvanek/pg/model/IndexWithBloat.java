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
 * Represents database index with information about bloat.
 *
 * @author Ivan Vakhrushev
 */
public class IndexWithBloat extends IndexWithSize implements BloatAware {

    private long bloatSizeInBytes;
    private int bloatPercentage;

    private IndexWithBloat(@Nonnull String tableName,
                           @Nonnull String indexName,
                           long indexSizeInBytes,
                           long bloatSizeInBytes,
                           int bloatPercentage) {
        super(tableName, indexName, indexSizeInBytes);
        this.bloatSizeInBytes = Validators.sizeNotNegative(bloatSizeInBytes, "bloatSizeInBytes");
        this.bloatPercentage = Validators.argumentNotNegative(bloatPercentage, "bloatPercentage");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBloatSizeInBytes() {
        return bloatSizeInBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBloatPercentage() {
        return bloatPercentage;
    }

    @Override
    protected String innerToString() {
        return super.innerToString() + ", bloatSizeInBytes=" + bloatSizeInBytes +
                ", bloatPercentage=" + bloatPercentage;
    }

    @Override
    public String toString() {
        return IndexWithBloat.class.getSimpleName() + '{' + innerToString() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        IndexWithBloat that = (IndexWithBloat) o;
        return bloatSizeInBytes == that.bloatSizeInBytes &&
                bloatPercentage == that.bloatPercentage;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bloatSizeInBytes, bloatPercentage);
    }

    @Nonnull
    public static IndexWithBloat of(@Nonnull String tableName,
                                    @Nonnull String indexName,
                                    long indexSizeInBytes,
                                    long bloatSizeInBytes,
                                    int bloatPercentage) {
        return new IndexWithBloat(tableName, indexName, indexSizeInBytes, bloatSizeInBytes, bloatPercentage);
    }
}
