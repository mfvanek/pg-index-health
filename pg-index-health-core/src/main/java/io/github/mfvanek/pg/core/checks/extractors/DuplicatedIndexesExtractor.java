/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.extractors;

import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.validation.Validators;

import java.sql.ResultSet;
import java.sql.SQLException;

import static io.github.mfvanek.pg.core.checks.extractors.TableExtractor.TABLE_NAME;

/**
 * A mapper from raw data to {@link DuplicatedIndexes} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public final class DuplicatedIndexesExtractor implements ResultSetExtractor<DuplicatedIndexes> {

    private final String targetColumnName;

    private DuplicatedIndexesExtractor(final String prefix) {
        this.targetColumnName = Validators.notBlank(prefix, "prefix") + "_indexes";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DuplicatedIndexes extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TABLE_NAME);
        final String duplicatedAsString = resultSet.getString(targetColumnName);
        return DuplicatedIndexes.of(tableName, duplicatedAsString);
    }

    /**
     * Creates a new {@code DuplicatedIndexesExtractor} with the specified prefix for the target column name.
     *
     * @param prefix the prefix to be used for the target column name; must be non-blank.
     * @return a {@code DuplicatedIndexesExtractor} configured with the given prefix.
     */
    public static ResultSetExtractor<DuplicatedIndexes> of(final String prefix) {
        return new DuplicatedIndexesExtractor(prefix);
    }
}
