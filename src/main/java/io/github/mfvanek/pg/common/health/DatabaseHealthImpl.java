/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health;

import io.github.mfvanek.pg.common.maintenance.MaintenanceFactory;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.index.maintenance.IndexesMaintenanceOnHost;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import io.github.mfvanek.pg.table.maintenance.TablesMaintenanceOnHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Implementation of {@code DatabaseHealth} which collects information from all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see IndexesMaintenanceOnHost
 */
public class DatabaseHealthImpl implements DatabaseHealth {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHealthImpl.class);

    private final IndexesMaintenanceOnHost indexesMaintenanceForPrimary;
    private final TablesMaintenanceOnHost tablesMaintenanceForPrimary;
    private final Collection<IndexesMaintenanceOnHost> indexesMaintenanceForAllHostsInCluster;
    private final Collection<TablesMaintenanceOnHost> tablesMaintenanceForAllHostsInCluster;

    public DatabaseHealthImpl(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                              @Nonnull final MaintenanceFactory maintenanceFactory) {
        Objects.requireNonNull(haPgConnection);
        Objects.requireNonNull(maintenanceFactory);
        this.indexesMaintenanceForPrimary = maintenanceFactory.forIndexes(haPgConnection.getConnectionToPrimary());
        this.tablesMaintenanceForPrimary = maintenanceFactory.forTables(haPgConnection.getConnectionToPrimary());
        final Set<PgConnection> pgConnections = haPgConnection.getConnectionsToAllHostsInCluster();
        this.indexesMaintenanceForAllHostsInCluster = maintenanceFactory.forIndexes(pgConnections);
        this.tablesMaintenanceForAllHostsInCluster = maintenanceFactory.forTables(pgConnections);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<Index> getInvalidIndexes(@Nonnull final PgContext pgContext) {
        logExecutingOnPrimary(indexesMaintenanceForPrimary.getHost());
        return indexesMaintenanceForPrimary.getInvalidIndexes(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull final PgContext pgContext) {
        logExecutingOnPrimary(indexesMaintenanceForPrimary.getHost());
        return indexesMaintenanceForPrimary.getDuplicatedIndexes(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull final PgContext pgContext) {
        logExecutingOnPrimary(indexesMaintenanceForPrimary.getHost());
        return indexesMaintenanceForPrimary.getIntersectedIndexes(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<UnusedIndex> getUnusedIndexes(@Nonnull final PgContext pgContext) {
        final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts = new ArrayList<>();
        for (IndexesMaintenanceOnHost maintenanceForHost : indexesMaintenanceForAllHostsInCluster) {
            potentiallyUnusedIndexesFromAllHosts.add(
                    doOnHost(maintenanceForHost.getHost(),
                            () -> maintenanceForHost.getUnusedIndexes(pgContext)));
        }
        return ReplicasHelper.getUnusedIndexesAsIntersectionResult(potentiallyUnusedIndexesFromAllHosts);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull final PgContext pgContext) {
        logExecutingOnPrimary(indexesMaintenanceForPrimary.getHost());
        return indexesMaintenanceForPrimary.getForeignKeysNotCoveredWithIndex(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull final PgContext pgContext) {
        final List<List<TableWithMissingIndex>> tablesWithMissingIndexesFromAllHosts = new ArrayList<>();
        for (TablesMaintenanceOnHost maintenanceForHost : tablesMaintenanceForAllHostsInCluster) {
            tablesWithMissingIndexesFromAllHosts.add(
                    doOnHost(maintenanceForHost.getHost(),
                            () -> maintenanceForHost.getTablesWithMissingIndexes(pgContext)));
        }
        return ReplicasHelper.getTablesWithMissingIndexesAsUnionResult(tablesWithMissingIndexesFromAllHosts);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<Table> getTablesWithoutPrimaryKey(@Nonnull final PgContext pgContext) {
        logExecutingOnPrimary(tablesMaintenanceForPrimary.getHost());
        return tablesMaintenanceForPrimary.getTablesWithoutPrimaryKey(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<IndexWithNulls> getIndexesWithNullValues(@Nonnull final PgContext pgContext) {
        logExecutingOnPrimary(indexesMaintenanceForPrimary.getHost());
        return indexesMaintenanceForPrimary.getIndexesWithNullValues(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<IndexWithBloat> getIndexesWithBloat(@Nonnull PgContext pgContext) {
        logExecutingOnPrimary(indexesMaintenanceForPrimary.getHost());
        return indexesMaintenanceForPrimary.getIndexesWithBloat(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public List<TableWithBloat> getTablesWithBloat(@Nonnull PgContext pgContext) {
        logExecutingOnPrimary(tablesMaintenanceForPrimary.getHost());
        return tablesMaintenanceForPrimary.getTablesWithBloat(pgContext);
    }

    private static void logExecutingOnPrimary(@Nonnull final PgHost host) {
        LOGGER.debug("Going to execute on primary host [{}]", host.getName());
    }

    private static <T> T doOnHost(@Nonnull final PgHost host, Supplier<T> action) {
        LOGGER.debug("Going to execute on host {}", host.getName());
        return action.get();
    }
}
