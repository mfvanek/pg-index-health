/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

/**
 * Abstract class to reduce code duplication.
 *
 * @author Ivan Vahrushev
 * @since 0.7.0
 */
abstract class AbstractTableAware implements DbObject, TableSizeAware {

    protected final Table table;

    AbstractTableAware(@Nonnull final Table table) {
        this.table = Validators.tableNonNull(table);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final String getName() {
        return table.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final String getTableName() {
        return table.getTableName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long getTableSizeInBytes() {
        return table.getTableSizeInBytes();
    }
}
