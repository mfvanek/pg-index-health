/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.utils;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.extension.RegisterExtension;

class QueryExecutorTestPg11 extends QueryExecutorTestBase {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {});

    QueryExecutorTestPg11() {
        super(embeddedPostgres.getTestDatabase());
    }
}
