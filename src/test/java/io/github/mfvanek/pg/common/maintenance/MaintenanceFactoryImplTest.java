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
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenance;
import io.github.mfvanek.pg.table.maintenance.TablesMaintenanceOnHost;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MaintenanceFactoryImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    private final MaintenanceFactory factory;
    private final PgConnection pgConnection;

    MaintenanceFactoryImplTest() {
        super(embeddedPostgres.getTestDatabase());
        this.factory = new MaintenanceFactoryImpl();
        this.pgConnection = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
    }

    @Test
    void forIndexes() {
        final IndexesMaintenanceOnHost maintenance = factory.forIndexes(pgConnection);
        assertNotNull(maintenance);
    }

    @Test
    void forTables() {
        final TablesMaintenanceOnHost maintenance = factory.forTables(pgConnection);
        assertNotNull(maintenance);
    }

    @Test
    void forStatistics() {
        final StatisticsMaintenance maintenance = factory.forStatistics(pgConnection);
        assertNotNull(maintenance);
    }
}
