/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support;

import java.util.concurrent.atomic.AtomicReference;

import static io.github.mfvanek.pg.model.context.PgContext.DEFAULT_SCHEMA_NAME;

/**
 * A scoped context holder for managing the active database schema name during test execution.
 */
public final class SchemaNameHolder implements AutoCloseable {

    private static final AtomicReference<String> SCHEMA_NAME_HOLDER = new AtomicReference<>(DEFAULT_SCHEMA_NAME);

    private final String oldSchemaName;

    private SchemaNameHolder(final String newSchemaName) {
        this.oldSchemaName = SCHEMA_NAME_HOLDER.getAndSet(newSchemaName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        SCHEMA_NAME_HOLDER.set(oldSchemaName);
    }

    /**
     * Gets the currently active schema name.
     *
     * @return the current schema name
     */
    public static String getSchemaName() {
        return SCHEMA_NAME_HOLDER.get();
    }

    /**
     * Creates a new scoped holder with the specified schema name.
     *
     * @param schemaName the schema name to set
     * @return a {@link SchemaNameHolder} instance that restores the previous schema name upon closing
     */
    public static SchemaNameHolder with(final String schemaName) {
        return new SchemaNameHolder(schemaName);
    }
}
