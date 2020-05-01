/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.index.maintenance.IndexesMaintenanceOnHost;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.table.maintenance.TablesMaintenanceOnHost;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        final Collection<StatisticsMaintenanceOnHost> maintenanceOnHosts =
                factory.forStatistics(Collections.singletonList(pgConnection));
        assertNotNull(maintenanceOnHosts);
        assertThat(maintenanceOnHosts, hasSize(1));
    }
}
