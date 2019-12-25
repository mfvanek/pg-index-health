/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.index.maintenance;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.PreparedDbExtension;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.Matchers.containsString;

class IndexMaintenanceImplTestPg11 extends IndexMaintenanceImplTestBase {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {});

    IndexMaintenanceImplTestPg11() {
        super(embeddedPostgres.getTestDatabase());
    }

    @Test
    void pgVersion() {
        MatcherAssert.assertThat(getPgVersion(), containsString("PostgreSQL 11.5"));
    }
}
