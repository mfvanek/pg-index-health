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
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link TableWithMissingIndex} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public final class TableWithMissingIndexExtractor implements ResultSetExtractor<TableWithMissingIndex> {

    private TableWithMissingIndexExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableWithMissingIndex extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TableExtractor.TABLE_NAME);
        final long tableSize = resultSet.getLong(TableExtractor.TABLE_SIZE);
        final long seqScans = resultSet.getLong("seq_scan");
        final long indexScans = resultSet.getLong("idx_scan");
        return TableWithMissingIndex.of(tableName, tableSize, seqScans, indexScans);
    }

    /**
     * Creates {@code TableWithMissingIndexExtractor} instance.
     *
     * @return {@code TableWithMissingIndexExtractor} instance.
     */
    public static ResultSetExtractor<TableWithMissingIndex> of() {
        return new TableWithMissingIndexExtractor();
    }
}
