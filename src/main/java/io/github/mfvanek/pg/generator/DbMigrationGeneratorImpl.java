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

import io.github.mfvanek.pg.model.index.ForeignKey;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Standard implementation of {@link DbMigrationGenerator}.
 *
 * @author Ivan Vahrushev
 * @since 0.5.0
 */
public class DbMigrationGeneratorImpl implements DbMigrationGenerator {

    static final String DELIMITER = "_";

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String generate(@Nonnull final List<ForeignKey> foreignKeys, @Nonnull final GeneratingOptions options) {
        Objects.requireNonNull(foreignKeys, "foreignKeys cannot be null");
        Objects.requireNonNull(options, "options cannot be null");

        final StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < foreignKeys.size(); ++i) {
            if (i != 0) {
                queryBuilder.append(System.lineSeparator())
                        .append(System.lineSeparator());
            }
            generate(queryBuilder, foreignKeys.get(i), options);
        }
        return queryBuilder.toString();
    }

    private void generate(@Nonnull final StringBuilder queryBuilder, @Nonnull final ForeignKey foreignKey, @Nonnull final GeneratingOptions options) {
        final PgIndexOnForeignKeyGenerator generator = PgIndexOnForeignKeyGenerator.of(foreignKey, options);
        queryBuilder.append(generator.generate());
    }
}
