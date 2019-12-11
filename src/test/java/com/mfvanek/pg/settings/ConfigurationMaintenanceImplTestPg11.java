package com.mfvanek.pg.settings;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.extension.RegisterExtension;

class ConfigurationMaintenanceImplTestPg11 extends ConfigurationMaintenanceImplTestBase {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension
                    .preparedDatabase(ds -> {})
                    .customize(builder -> builder.setServerConfig(ImportantParam.LOCK_TIMEOUT.getName(), "1000"));

    ConfigurationMaintenanceImplTestPg11() {
        super(embeddedPostgres.getTestDatabase());
    }
}
