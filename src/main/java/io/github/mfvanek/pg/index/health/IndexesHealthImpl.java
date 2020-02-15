/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.index.health;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.index.maintenance.IndexMaintenance;
import io.github.mfvanek.pg.index.maintenance.MaintenanceFactory;
import io.github.mfvanek.pg.index.maintenance.StatisticsMaintenance;
import io.github.mfvanek.pg.model.DuplicatedIndexes;
import io.github.mfvanek.pg.model.ForeignKey;
import io.github.mfvanek.pg.model.Index;
import io.github.mfvanek.pg.model.IndexWithBloat;
import io.github.mfvanek.pg.model.IndexWithNulls;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.Table;
import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.model.UnusedIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Implementation of {@code IndexesHealth} which collects information from all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see IndexMaintenance
 */
public class IndexesHealthImpl implements IndexesHealth {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexesHealthImpl.class);

    private final IndexMaintenance maintenanceForMaster;
    private final List<IndexMaintenance> indexMaintenanceForAllHostsInCluster;
    private final List<StatisticsMaintenance> statisticsMaintenanceForAllHostsInCluster;

    public IndexesHealthImpl(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                             @Nonnull final MaintenanceFactory maintenanceFactory) {
        Objects.requireNonNull(haPgConnection);
        Objects.requireNonNull(maintenanceFactory);
        this.maintenanceForMaster = maintenanceFactory.forIndex(haPgConnection.getConnectionToMaster());
        this.indexMaintenanceForAllHostsInCluster = ReplicasHelper.createIndexMaintenanceForReplicas(
                haPgConnection.getConnectionsToAllHostsInCluster(), maintenanceFactory);
        this.statisticsMaintenanceForAllHostsInCluster = ReplicasHelper.createStatisticsMaintenanceForReplicas(
                haPgConnection.getConnectionsToAllHostsInCluster(), maintenanceFactory);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<Index> getInvalidIndexes(@Nonnull final PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getInvalidIndexes(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull final PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getDuplicatedIndexes(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull final PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getIntersectedIndexes(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<UnusedIndex> getUnusedIndexes(@Nonnull final PgContext pgContext) {
        final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts = new ArrayList<>();
        for (IndexMaintenance maintenanceForHost : indexMaintenanceForAllHostsInCluster) {
            potentiallyUnusedIndexesFromAllHosts.add(
                    doOnHost(maintenanceForHost.getHost(),
                            () -> maintenanceForHost.getPotentiallyUnusedIndexes(pgContext)));
        }
        return ReplicasHelper.getUnusedIndexesAsIntersectionResult(potentiallyUnusedIndexesFromAllHosts);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull final PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getForeignKeysNotCoveredWithIndex(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull final PgContext pgContext) {
        final List<List<TableWithMissingIndex>> tablesWithMissingIndexesFromAllHosts = new ArrayList<>();
        for (IndexMaintenance maintenanceForHost : indexMaintenanceForAllHostsInCluster) {
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
        logExecutingOnMaster();
        return maintenanceForMaster.getTablesWithoutPrimaryKey(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<IndexWithNulls> getIndexesWithNullValues(@Nonnull final PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getIndexesWithNullValues(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public List<IndexWithBloat> getIndexesWithBloat(@Nonnull PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getIndexesWithBloat(pgContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetStatistics() {
        for (StatisticsMaintenance statisticsMaintenance : statisticsMaintenanceForAllHostsInCluster) {
            doOnHost(statisticsMaintenance.getHost(), statisticsMaintenance::resetStatistics);
        }
    }

    private void logExecutingOnMaster() {
        LOGGER.debug("Going to execute on master host [{}]", maintenanceForMaster.getHost().getName());
    }

    private <T> T doOnHost(@Nonnull final PgHost host, Supplier<T> action) {
        LOGGER.debug("Going to execute on host {}", host.getName());
        return action.get();
    }
}
