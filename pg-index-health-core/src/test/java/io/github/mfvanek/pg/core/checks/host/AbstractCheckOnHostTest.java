/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.connection.exception.PgSqlException;
import io.github.mfvanek.pg.connection.fixtures.support.LogsCaptor;
import io.github.mfvanek.pg.core.checks.common.ExecutionTopology;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.utils.QueryExecutors;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("checkstyle:AbstractClassName")
class AbstractCheckOnHostTest extends DatabaseAwareTestBase {

    private final AbstractCheckOnHost<@NonNull IndexWithColumns> check = new IndexesWithNullValuesCheckOnHost(getPgConnection());

    @ParameterizedTest
    @ValueSource(strings = PgContext.DEFAULT_SCHEMA_NAME)
    void securityTest(final String schemaName) {
        try (LogsCaptor ignored = new LogsCaptor(QueryExecutors.class, Level.FINEST)) {
            executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withNullValuesInIndex(), ctx -> {
                final long before = getRowsCount(ctx.getSchemaName(), "clients");
                assertThat(before).isEqualTo(1001L);
                assertThat(check.check(PgContext.of("; truncate table clients;")))
                    .isEmpty();
                assertThat(getRowsCount(ctx.getSchemaName(), "clients")).isEqualTo(before);

                assertThat(check.check(PgContext.of("; select pg_sleep(100000000);")))
                    .isEmpty();

                assertThat(check.check()) // executing on default schema
                    .hasSize(1)
                    .containsExactly(
                        IndexWithColumns.ofNullable(PgContext.ofDefault(), "clients", "i_clients_middle_name", "middle_name"));

                assertThat(check.check(t -> !"clients".equalsIgnoreCase(t.getTableName())))
                    .isEmpty();

                assertThat(check.getExecutionTopology())
                    .isEqualTo(ExecutionTopology.ON_PRIMARY);
                assertThat(check.isAcrossCluster())
                    .isFalse();
            });
        }
    }

    private long getRowsCount(final String schemaName,
                              final String tableName) {
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
