/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.index.health.logger;

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.extension.RegisterExtension;

class IndexesHealthLoggerTestPg10 extends IndexesHealthLoggerTestBase {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {});

    IndexesHealthLoggerTestPg10() {
        super(embeddedPostgres.getTestDatabase());
    }
}
