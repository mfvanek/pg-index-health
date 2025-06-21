/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;

import java.util.Objects;

/**
 * Abstract base class for database objects that are associated with a specific table.
 *
 * @author Ivan Vakhrushev
 * @since 0.7.0
 */
abstract class AbstractTableAware implements DbObject, TableSizeAware {

    /**
     * The {@link Table} instance associated with this object.
     */
    protected final Table table;

    AbstractTableAware(final Table table) {
        this.table = Objects.requireNonNull(table, "table cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getName() {
        return table.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PgObjectType getObjectType() {
        return table.getObjectType();
    }

    /**
     * {@inheritDoc}
     */
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
