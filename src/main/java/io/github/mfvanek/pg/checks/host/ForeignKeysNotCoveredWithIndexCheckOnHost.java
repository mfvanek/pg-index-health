/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.table.Column;
import io.github.mfvanek.pg.utils.ColumnsInForeignKeyParser;

import java.sql.Array;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for foreign keys without associated indexes on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.6.0
 */
public class ForeignKeysNotCoveredWithIndexCheckOnHost extends AbstractCheckOnHost<ForeignKey> {

    public ForeignKeysNotCoveredWithIndexCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(ForeignKey.class, pgConnection, Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX);
    }

    /**
     * Returns foreign keys without associated indexes in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of foreign keys without associated indexes
     */
    @Nonnull
    @Override
    public List<ForeignKey> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final String constraintName = rs.getString("constraint_name");
            final Array columnsArray = rs.getArray("columns");
            final String[] rawColumns = (String[]) columnsArray.getArray();
            final List<Column> columns = ColumnsInForeignKeyParser.parseRawColumnData(tableName, rawColumns);
            return ForeignKey.of(tableName, constraintName, columns);
        });
    }
}
