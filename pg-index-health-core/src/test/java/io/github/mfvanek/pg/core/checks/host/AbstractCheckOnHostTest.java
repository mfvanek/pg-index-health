/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.connection.exception.PgSqlException;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("checkstyle:AbstractClassName")
class AbstractCheckOnHostTest extends DatabaseAwareTestBase {

    private final AbstractCheckOnHost<IndexWithNulls> check = new IndexesWithNullValuesCheckOnHost(getPgConnection());

    @ParameterizedTest
    @ValueSource(strings = PgContext.DEFAULT_SCHEMA_NAME)
    void securityTest(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withNullValuesInIndex(), ctx -> {
            final long before = getRowsCount(ctx.getSchemaName(), "clients");
            assertThat(before).isEqualTo(1001L);
            assertThat(check.check(PgContext.of("; truncate table clients;")))
                .isEmpty();
            assertThat(getRowsCount(ctx.getSchemaName(), "clients")).isEqualTo(before);

            assertThat(check.check(PgContext.of("; select pg_sleep(100000000);")))
                .isEmpty();

            assertThat(check.check()) // executing on public schema by default
                .hasSize(1)
                .containsExactly(
                    IndexWithNulls.of("clients", "i_clients_middle_name", 0L, "middle_name"));

            assertThat(check.check(t -> !"clients".equalsIgnoreCase(t.getTableName())))
                .isEmpty();
        });
    }

    private long getRowsCount(@Nonnull final String schemaName,
                              @Nonnull final String tableName) {
        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(
                "select count(*) from " + schemaName + '.' + tableName)) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }
}
