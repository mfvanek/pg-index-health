/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.extractors;

import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link DuplicatedForeignKeys} model.
 *
 * @author Ivan Vakhrushev
 * @see ForeignKeyExtractor
 * @since 0.13.1
 */
public final class DuplicatedForeignKeysExtractor implements ResultSetExtractor<DuplicatedForeignKeys> {

    private final ResultSetExtractor<ForeignKey> defaultExtractor;
    private final ResultSetExtractor<ForeignKey> duplicateKeyExtractor;

    private DuplicatedForeignKeysExtractor(final String prefix) {
        this.defaultExtractor = ForeignKeyExtractor.ofDefault();
        this.duplicateKeyExtractor = ForeignKeyExtractor.withPrefix(prefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DuplicatedForeignKeys extractData(final ResultSet resultSet) throws SQLException {
        final ForeignKey first = defaultExtractor.extractData(resultSet);
        final ForeignKey second = duplicateKeyExtractor.extractData(resultSet);
        return DuplicatedForeignKeys.of(first, second);
    }

    /**
     * Creates {@code DuplicatedForeignKeysExtractor} instance.
     *
     * @param prefix given prefix; must be non-null
     * @return {@code DuplicatedForeignKeysExtractor} instance
     */
    public static ResultSetExtractor<DuplicatedForeignKeys> of(final String prefix) {
        return new DuplicatedForeignKeysExtractor(prefix);
    }
}
