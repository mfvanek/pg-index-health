/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

class IndexMaintenanceImplTestPg11 extends IndexMaintenanceImplTestBase {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {
            });

    IndexMaintenanceImplTestPg11() {
        super(embeddedPostgres.getTestDatabase());
    }

    @Test
    void pgVersion() throws SQLException {
        try (var databasePopulator = createDatabasePopulator()) {
            assertThat(databasePopulator.getPgVersion(), containsString("PostgreSQL 11.5"));
        }
    }
}
