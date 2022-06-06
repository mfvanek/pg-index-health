/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index;

import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for intersected (partially identical) indexes on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.5.1
 */
public class IntersectedIndexesCheckOnHost extends AbstractCheckOnHost<DuplicatedIndexes> {

    public IntersectedIndexesCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(pgConnection, Diagnostic.INTERSECTED_INDEXES);
    }

    /**
     * Returns intersected (partially identical) indexes in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of intersected indexes
     */
    @Nonnull
    @Override
    public List<DuplicatedIndexes> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final String duplicatedAsString = rs.getString("intersected_indexes");
            return DuplicatedIndexes.of(tableName, duplicatedAsString);
        });
    }
}
