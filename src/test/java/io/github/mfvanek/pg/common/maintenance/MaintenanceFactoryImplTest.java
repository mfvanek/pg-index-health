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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaintenanceFactoryImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    private final MaintenanceFactory factory;
    private final PgConnection pgConnection;

    MaintenanceFactoryImplTest() {
        super(embeddedPostgres.getTestDatabase());
        this.factory = new MaintenanceFactoryImpl();
        this.pgConnection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
    }

    @Test
    void forIndexes() {
        final IndexesMaintenanceOnHost maintenance = factory.forIndexes(pgConnection);
        assertNotNull(maintenance);

        final Collection<IndexesMaintenanceOnHost> maintenanceOnHosts =
                factory.forIndexes(Collections.singletonList(pgConnection));
        assertNotNull(maintenanceOnHosts);
        assertThat(maintenanceOnHosts, hasSize(1));
    }

    @Test
    void forTables() {
        final TablesMaintenanceOnHost maintenance = factory.forTables(pgConnection);
        assertNotNull(maintenance);

        final Collection<TablesMaintenanceOnHost> maintenanceOnHosts =
                factory.forTables(Collections.singletonList(pgConnection));
        assertNotNull(maintenanceOnHosts);
        assertThat(maintenanceOnHosts, hasSize(1));
    }

    @Test
    void forStatistics() {
        final StatisticsMaintenanceOnHost maintenance = factory.forStatistics(pgConnection);
        assertNotNull(maintenance);
    }

    @Test
    void forStatisticsByHost() {
        final Map<PgHost, StatisticsMaintenanceOnHost> maintenanceByHost =
                factory.forStatistics(Collections.singletonList(pgConnection));
        assertNotNull(maintenanceByHost);
        assertEquals(1, maintenanceByHost.size());
        assertThat(maintenanceByHost.keySet().iterator().next(), equalTo(pgConnection.getHost()));
    }

    @Test
    void forStatisticsByHostEmpty() {
        final Map<PgHost, StatisticsMaintenanceOnHost> maintenanceByHost =
                factory.forStatistics(Collections.emptyList());
        assertNotNull(maintenanceByHost);
        assertTrue(maintenanceByHost.isEmpty());
    }

    @Test
    void forConfiguration() {
        final ConfigurationMaintenanceOnHost maintenance = factory.forConfiguration(pgConnection);
        assertNotNull(maintenance);
    }

    @Test
    void forConfigurationByHost() {
        final Map<PgHost, ConfigurationMaintenanceOnHost> maintenanceByHost =
                factory.forConfiguration(Collections.singletonList(pgConnection));
        assertNotNull(maintenanceByHost);
        assertEquals(1, maintenanceByHost.size());
        assertThat(maintenanceByHost.keySet().iterator().next(), equalTo(pgConnection.getHost()));
    }

    @Test
    void forConfigurationByHostEmpty() {
        final Map<PgHost, ConfigurationMaintenanceOnHost> maintenanceByHost =
                factory.forConfiguration(Collections.emptyList());
        assertNotNull(maintenanceByHost);
        assertTrue(maintenanceByHost.isEmpty());
    }
}
