/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.column.ColumnWithSerialType;

/**
 * Migration generator for removing default value and sequence on columns of serial types.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.2
 */
public final class ColumnWithSerialTypeMigrationGenerator extends AbstractDbMigrationGenerator<ColumnWithSerialType> {

    private final DropDefaultValueGenerator dropDefaultValueGenerator;
    private final DropSequenceGenerator dropSequenceGenerator;

    /**
     * Constructs a migration generator for handling columns with serial types. It initializes
     * components for generating SQL queries to drop default values and associated sequences
     * on such columns.
     *
     * @param options the options to configure the generation of SQL queries, such as formatting
     *                and optional behaviors.
     */
    public ColumnWithSerialTypeMigrationGenerator(final GeneratingOptions options) {
        this.dropDefaultValueGenerator = new DropDefaultValueGenerator(options);
        this.dropSequenceGenerator = new DropSequenceGenerator(options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String generate(final ColumnWithSerialType column) {
        return dropDefaultValueGenerator.generate(column) +
            System.lineSeparator() +
            dropSequenceGenerator.generate(column);
    }
}
