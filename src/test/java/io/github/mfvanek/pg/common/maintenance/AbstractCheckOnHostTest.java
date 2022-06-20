/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.checks.host.IndexesWithNullValuesCheckOnHost;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractCheckOnHostTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final AbstractCheckOnHost<IndexWithNulls> check;

    AbstractCheckOnHostTest() {
        super(POSTGRES.getTestDatabase());
        this.check = new IndexesWithNullValuesCheckOnHost(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase()));
    }

    @Test
    void securityTest() {
        executeTestOnDatabase("public", dbp -> dbp.withReferences().withData().withNullValuesInIndex(), ctx -> {
            final long before = getRowsCount(ctx.getSchemaName(), "clients");
            assertThat(before).isEqualTo(1001L);
            assertThat(check.check(PgContext.of("; truncate table clients;")))
                    .isNotNull()
                    .isEmpty();
            assertThat(getRowsCount(ctx.getSchemaName(), "clients")).isEqualTo(before);

            assertThat(check.check(PgContext.of("; select pg_sleep(100000000);")))
                    .isNotNull()
                    .isEmpty();

            assertThat(check.check(ctx))
                    .isNotNull()
                    .hasSize(1);
        });
    }
}
