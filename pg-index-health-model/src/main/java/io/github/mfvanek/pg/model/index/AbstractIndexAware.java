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

import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Abstract base class for database objects that are associated with a specific index.
 *
 * @author Ivan Vakhrushev
 * @since 0.15.0
 */
abstract class AbstractIndexAware implements DbObject, TableNameAware, IndexSizeAware {

    /**
     * The {@link Index} instance associated with this object.
     */
    protected final Index index;

    AbstractIndexAware(@Nonnull final Index index) {
        this.index = Objects.requireNonNull(index, "index cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final String getTableName() {
        return index.getTableName();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final String getName() {
        return index.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final PgObjectType getObjectType() {
        return index.getObjectType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long getIndexSizeInBytes() {
        return index.getIndexSizeInBytes();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final String getIndexName() {
        return index.getIndexName();
    }
}
