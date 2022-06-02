/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.index.maintenance.IndexesMaintenanceOnHost;
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHost;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.table.maintenance.TablesMaintenanceOnHost;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

class MaintenanceFactoryImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final MaintenanceFactory factory;
    private final PgConnection pgConnection;

    MaintenanceFactoryImplTest() {
        super(POSTGRES.getTestDatabase());
        this.factory = new MaintenanceFactoryImpl();
        this.pgConnection = PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase());
    }

    @Test
    void forIndexes() {
        final IndexesMaintenanceOnHost maintenance = factory.forIndexes(pgConnection);
        assertThat(maintenance).isNotNull();
    }

    @Test
    void forIndexesByHost() {
        final Map<PgHost, IndexesMaintenanceOnHost> maintenanceOnHosts = factory.forIndexes(Collections.singletonList(pgConnection));
        checkThatContainsOneItem(maintenanceOnHosts);
    }

    @Test
    void forIndexesByHostEmpty() {
        final Map<PgHost, IndexesMaintenanceOnHost> maintenanceOnHosts = factory.forIndexes(Collections.emptyList());
        checkThatEmpty(maintenanceOnHosts);
    }

    @Test
    void forTables() {
        final TablesMaintenanceOnHost maintenance = factory.forTables(pgConnection);
        assertThat(maintenance).isNotNull();
    }

    @Test
    void forTablesByHost() {
        final Map<PgHost, TablesMaintenanceOnHost> maintenanceOnHosts = factory.forTables(Collections.singletonList(pgConnection));
        checkThatContainsOneItem(maintenanceOnHosts);
    }

    @Test
    void forTablesByHostEmpty() {
        final Map<PgHost, TablesMaintenanceOnHost> maintenanceOnHosts = factory.forTables(Collections.emptyList());
        checkThatEmpty(maintenanceOnHosts);
    }

    @Test
    void forStatistics() {
        final StatisticsMaintenanceOnHost maintenance = factory.forStatistics(pgConnection);
        assertThat(maintenance).isNotNull();
    }

    @Test
    void forStatisticsByHost() {
        final Map<PgHost, StatisticsMaintenanceOnHost> maintenanceOnHosts = factory.forStatistics(Collections.singletonList(pgConnection));
        checkThatContainsOneItem(maintenanceOnHosts);
    }

    @Test
    void forStatisticsByHostEmpty() {
        final Map<PgHost, StatisticsMaintenanceOnHost> maintenanceOnHosts = factory.forStatistics(Collections.emptyList());
        checkThatEmpty(maintenanceOnHosts);
    }

    @Test
    void forConfiguration() {
        final ConfigurationMaintenanceOnHost maintenance = factory.forConfiguration(pgConnection);
        assertThat(maintenance).isNotNull();
    }

    @Test
    void forConfigurationByHost() {
        final Map<PgHost, ConfigurationMaintenanceOnHost> maintenanceOnHosts = factory.forConfiguration(Collections.singletonList(pgConnection));
        checkThatContainsOneItem(maintenanceOnHosts);
    }

    @Test
    void forConfigurationByHostEmpty() {
        final Map<PgHost, ConfigurationMaintenanceOnHost> maintenanceOnHosts = factory.forConfiguration(Collections.emptyList());
        checkThatEmpty(maintenanceOnHosts);
    }

    private <T> void checkThatContainsOneItem(@Nonnull final Map<PgHost, T> maintenanceOnHosts) {
        assertThat(maintenanceOnHosts)
                .isNotNull()
                .hasSize(1);
        assertThat(maintenanceOnHosts.keySet().iterator().next()).isEqualTo(pgConnection.getHost());
    }

    private <T> void checkThatEmpty(@Nonnull final Map<PgHost, T> maintenanceOnHosts) {
        assertThat(maintenanceOnHosts)
                .isNotNull()
                .isEmpty();
    }
}
