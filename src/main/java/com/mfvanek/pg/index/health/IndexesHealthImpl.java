/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.connection.HighAvailabilityPgConnection;
import com.mfvanek.pg.connection.PgHost;
import com.mfvanek.pg.index.maintenance.IndexMaintenance;
import com.mfvanek.pg.index.maintenance.MaintenanceFactory;
import com.mfvanek.pg.index.maintenance.StatisticsMaintenance;
import com.mfvanek.pg.model.DuplicatedIndexes;
import com.mfvanek.pg.model.ForeignKey;
import com.mfvanek.pg.model.Index;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.PgContext;
import com.mfvanek.pg.model.Table;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.UnusedIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class IndexesHealthImpl implements IndexesHealth {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexesHealthImpl.class);

    private final IndexMaintenance maintenanceForMaster;
    private final List<IndexMaintenance> maintenanceForReplicas;
    private final List<StatisticsMaintenance> statisticsMaintenanceForReplicas;

    public IndexesHealthImpl(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                             @Nonnull final MaintenanceFactory maintenanceFactory) {
        Objects.requireNonNull(haPgConnection);
        Objects.requireNonNull(maintenanceFactory);
        this.maintenanceForMaster = maintenanceFactory.forIndex(haPgConnection.getConnectionToMaster());
        this.maintenanceForReplicas = ReplicasHelper.createIndexMaintenanceForReplicas(
                haPgConnection.getConnectionsToReplicas(), maintenanceFactory);
        this.statisticsMaintenanceForReplicas = ReplicasHelper.createStatisticsMaintenanceForReplicas(
                haPgConnection.getConnectionsToReplicas(), maintenanceFactory);
    }

    @Nonnull
    @Override
    public List<Index> getInvalidIndexes(@Nonnull final PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getInvalidIndexes(pgContext);
    }

    @Nonnull
    @Override
    public List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull final PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getDuplicatedIndexes(pgContext);
    }

    @Nonnull
    @Override
    public List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull final PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getIntersectedIndexes(pgContext);
    }

    @Nonnull
    @Override
    public List<UnusedIndex> getUnusedIndexes(@Nonnull final PgContext pgContext) {
        final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts = new ArrayList<>();
        for (var maintenanceForReplica : maintenanceForReplicas) {
            potentiallyUnusedIndexesFromAllHosts.add(
                    doOnHost(maintenanceForReplica.getHost(),
                            () -> maintenanceForReplica.getPotentiallyUnusedIndexes(pgContext)));
        }
        return ReplicasHelper.getUnusedIndexesAsIntersectionResult(potentiallyUnusedIndexesFromAllHosts);
    }

    @Nonnull
    @Override
    public List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull final PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getForeignKeysNotCoveredWithIndex(pgContext);
    }

    @Nonnull
    @Override
    public List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull final PgContext pgContext) {
        final List<List<TableWithMissingIndex>> tablesWithMissingIndexesFromAllHosts = new ArrayList<>();
        for (var maintenanceForReplica : maintenanceForReplicas) {
            tablesWithMissingIndexesFromAllHosts.add(
                    doOnHost(maintenanceForReplica.getHost(),
                            () -> maintenanceForReplica.getTablesWithMissingIndexes(pgContext)));
        }
        return ReplicasHelper.getTablesWithMissingIndexesAsUnionResult(tablesWithMissingIndexesFromAllHosts);
    }

    @Nonnull
    @Override
    public List<Table> getTablesWithoutPrimaryKey(@Nonnull final PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getTablesWithoutPrimaryKey(pgContext);
    }

    @Nonnull
    @Override
    public List<IndexWithNulls> getIndexesWithNullValues(@Nonnull final PgContext pgContext) {
        logExecutingOnMaster();
        return maintenanceForMaster.getIndexesWithNullValues(pgContext);
    }

    @Override
    public void resetStatistics() {
        for (var statisticsMaintenance : statisticsMaintenanceForReplicas) {
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
