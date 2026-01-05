/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.constraint.ForeignKey;

/**
 * Migration generator for creating indexes covering foreign keys.
 *
 * @author Ivan Vakhrushev
 * @since 0.5.0
 */
public final class ForeignKeyMigrationGenerator extends AbstractDbMigrationGenerator<ForeignKey> {

    private final PgIndexOnForeignKeyGenerator generator;

    public ForeignKeyMigrationGenerator(final GeneratingOptions options) {
        this.generator = new PgIndexOnForeignKeyGenerator(options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String generate(final ForeignKey foreignKey) {
        return generator.generate(foreignKey);
    }
}
