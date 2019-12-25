/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.index.health;

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

class IndexesHealthImplTestPg10 extends IndexesHealthImplTestBase {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {});

    IndexesHealthImplTestPg10() {
        super(embeddedPostgres.getTestDatabase());
    }

    @Test
    void pgVersion() {
        assertThat(getPgVersion(), containsString("PostgreSQL 10.6"));
    }
}
