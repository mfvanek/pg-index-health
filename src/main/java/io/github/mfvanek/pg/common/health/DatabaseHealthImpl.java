/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health;

import io.github.mfvanek.pg.common.maintenance.MaintenanceFactory;
import io.github.mfvanek.pg.common.management.AbstractManagement;
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
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.table.maintenance.TablesMaintenanceOnHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Implementation of {@code DatabaseHealth} which collects information from all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see IndexesMaintenanceOnHost
 */
public class DatabaseHealthImpl extends AbstractManagement implements DatabaseHealth {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHealthImpl.class);

    private final Map<PgHost, IndexesMaintenanceOnHost> indexesMaintenanceForAllHostsInCluster;
    private final Map<PgHost, TablesMaintenanceOnHost> tablesMaintenanceForAllHostsInCluster;
    private final Map<PgHost, StatisticsMaintenanceOnHost> statisticsMaintenanceForAllHostsInCluster;

    public DatabaseHealthImpl(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                              @Nonnull final MaintenanceFactory maintenanceFactory) {
        super(haPgConnection);
        Objects.requireNonNull(maintenanceFactory);
        final Set<PgConnection> pgConnections = haPgConnection.getConnectionsToAllHostsInCluster();
        this.indexesMaintenanceForAllHostsInCluster = maintenanceFactory.forIndexes(pgConnections);
        this.tablesMaintenanceForAllHostsInCluster = maintenanceFactory.forTables(pgConnections);
        this.statisticsMaintenanceForAllHostsInCluster = maintenanceFactory.forStatistics(pgConnections);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<Index> getInvalidIndexes(@Nonnull final PgContext pgContext) {
        return doOnPrimary(indexesMaintenanceForAllHostsInCluster, IndexesMaintenanceOnHost::getInvalidIndexes, pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull final PgContext pgContext) {
        return doOnPrimary(indexesMaintenanceForAllHostsInCluster, IndexesMaintenanceOnHost::getDuplicatedIndexes, pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull final PgContext pgContext) {
        return doOnPrimary(indexesMaintenanceForAllHostsInCluster, IndexesMaintenanceOnHost::getIntersectedIndexes, pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<UnusedIndex> getUnusedIndexes(@Nonnull final PgContext pgContext) {
        final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts = new ArrayList<>();
        for (IndexesMaintenanceOnHost maintenanceForHost : indexesMaintenanceForAllHostsInCluster.values()) {
            final PgHost currentHost = maintenanceForHost.getHost();
            final List<UnusedIndex> unusedIndexesFromCurrentHost = doOnHost(currentHost,
                    () -> {
                        logLastStatsResetDate(currentHost);
                        return maintenanceForHost.getUnusedIndexes(pgContext);
                    });
            potentiallyUnusedIndexesFromAllHosts.add(unusedIndexesFromCurrentHost);
        }
        return ReplicasHelper.getUnusedIndexesAsIntersectionResult(potentiallyUnusedIndexesFromAllHosts);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull final PgContext pgContext) {
        return doOnPrimary(indexesMaintenanceForAllHostsInCluster, IndexesMaintenanceOnHost::getForeignKeysNotCoveredWithIndex, pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull final PgContext pgContext) {
        final List<List<TableWithMissingIndex>> tablesWithMissingIndexesFromAllHosts = new ArrayList<>();
        for (TablesMaintenanceOnHost maintenanceForHost : tablesMaintenanceForAllHostsInCluster.values()) {
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
        return doOnPrimary(tablesMaintenanceForAllHostsInCluster, TablesMaintenanceOnHost::getTablesWithoutPrimaryKey, pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<IndexWithNulls> getIndexesWithNullValues(@Nonnull final PgContext pgContext) {
        return doOnPrimary(indexesMaintenanceForAllHostsInCluster, IndexesMaintenanceOnHost::getIndexesWithNullValues, pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<IndexWithBloat> getIndexesWithBloat(@Nonnull PgContext pgContext) {
        return doOnPrimary(indexesMaintenanceForAllHostsInCluster, IndexesMaintenanceOnHost::getIndexesWithBloat, pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public List<TableWithBloat> getTablesWithBloat(@Nonnull PgContext pgContext) {
        return doOnPrimary(tablesMaintenanceForAllHostsInCluster, TablesMaintenanceOnHost::getTablesWithBloat, pgContext);
    }

    private void logLastStatsResetDate(@Nonnull final PgHost host) {
        LOGGER.info(ReplicasHelper.getLastStatsResetDateLogMessage(host, statisticsMaintenanceForAllHostsInCluster));
    }
}
