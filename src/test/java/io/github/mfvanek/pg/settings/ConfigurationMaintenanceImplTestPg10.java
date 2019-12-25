/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.settings;

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.extension.RegisterExtension;

class ConfigurationMaintenanceImplTestPg10 extends ConfigurationMaintenanceImplTestBase {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension
                    .preparedDatabase(ds -> {})
                    .customize(builder -> builder.setServerConfig(ImportantParam.LOCK_TIMEOUT.getName(), "1000"));

    ConfigurationMaintenanceImplTestPg10() {
        super(embeddedPostgres.getTestDatabase());
    }
}
