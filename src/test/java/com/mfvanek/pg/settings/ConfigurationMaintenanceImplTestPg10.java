package com.mfvanek.pg.settings;

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.extension.RegisterExtension;

class ConfigurationMaintenanceImplTestPg10 extends ConfigurationMaintenanceImplTestBase {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {
            });

    ConfigurationMaintenanceImplTestPg10() {
        super(embeddedPostgres.getTestDatabase());
    }
}
