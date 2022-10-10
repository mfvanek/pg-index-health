/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.column.ColumnWithSerialType;

import javax.annotation.Nonnull;

/**
 * Migration generator for removing default value and sequence on columns of serial types.
 *
 * @author Ivan Vahrushev
 * @since 0.6.2
 */
public class ColumnWithSerialTypeMigrationGenerator extends AbstractDbMigrationGenerator<ColumnWithSerialType> {

    private final DropDefaultValueGenerator dropDefaultValueGenerator;
    private final DropSequenceGenerator dropSequenceGenerator;

    public ColumnWithSerialTypeMigrationGenerator(@Nonnull final GeneratingOptions options) {
        this.dropDefaultValueGenerator = new DropDefaultValueGenerator(options);
        this.dropSequenceGenerator = new DropSequenceGenerator(options);
    }

    @Override
    protected void generate(@Nonnull final StringBuilder queryBuilder, @Nonnull final ColumnWithSerialType column) {
        queryBuilder.append(dropDefaultValueGenerator.generate(column))
                .append(System.lineSeparator())
                .append(dropSequenceGenerator.generate(column));
    }
}
