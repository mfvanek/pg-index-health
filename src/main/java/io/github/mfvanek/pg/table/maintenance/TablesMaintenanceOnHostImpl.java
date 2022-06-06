/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.table.maintenance;

import io.github.mfvanek.pg.common.maintenance.AbstractMaintenance;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Implementation of {@code TableMaintenanceOnHost} which collects information from the current host in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 * @see PgHost
 */
public class TablesMaintenanceOnHostImpl extends AbstractMaintenance implements TablesMaintenanceOnHost {

    private static final String TABLE_SIZE = "table_size";

    public TablesMaintenanceOnHostImpl(@Nonnull final PgConnection pgConnection) {
        super(pgConnection);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull final PgContext pgContext) {
        return executeQuery(Diagnostic.TABLES_WITH_MISSING_INDEXES, pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final long tableSize = rs.getLong(TABLE_SIZE);
            final long seqScans = rs.getLong("seq_scan");
            final long indexScans = rs.getLong("idx_scan");
            return TableWithMissingIndex.of(tableName, tableSize, seqScans, indexScans);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<Table> getTablesWithoutPrimaryKey(@Nonnull final PgContext pgContext) {
        return executeQuery(Diagnostic.TABLES_WITHOUT_PRIMARY_KEY, pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final long tableSize = rs.getLong(TABLE_SIZE);
            return Table.of(tableName, tableSize);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public List<TableWithBloat> getTablesWithBloat(@Nonnull final PgContext pgContext) {
        return executeQuery(Diagnostic.BLOATED_TABLES, pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final long tableSize = rs.getLong(TABLE_SIZE);
            final long bloatSize = rs.getLong(BLOAT_SIZE);
            final int bloatPercentage = rs.getInt(BLOAT_PERCENTAGE);
            return TableWithBloat.of(tableName, tableSize, bloatSize, bloatPercentage);
        });
    }
}
