/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.checks.extractors.ColumnExtractor;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for B-tree indexes on array columns.
 * GIN-index should be used instead for such columns.
 *
 * @author Vadim Khizhin
 * @since 0.10.4
 */
public class BtreeIndexesOnArrayColumnsCheckOnHost extends AbstractCheckOnHost<IndexWithColumns> {

    public BtreeIndexesOnArrayColumnsCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(IndexWithColumns.class, pgConnection, Diagnostic.BTREE_INDEXES_ON_ARRAY_COLUMNS);
    }

    @Nonnull
    @Override
    public List<IndexWithColumns> check(@Nonnull final PgContext pgContext) {
        final ResultSetExtractor<Column> columnExtractor = ColumnExtractor.of();
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final String indexName = rs.getString(INDEX_NAME);
            final long indexSize = rs.getLong(INDEX_SIZE);
            final Column column = columnExtractor.extractData(rs);
            return IndexWithColumns.ofSingle(tableName, indexName, indexSize, column);
        });
    }
}
