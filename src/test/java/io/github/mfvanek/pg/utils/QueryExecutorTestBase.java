/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.model.PgContext;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class QueryExecutorTestBase extends DatabaseAwareTestBase {

    private final PgConnection pgConnection;

    QueryExecutorTestBase(@Nonnull final DataSource dataSource) {
        super(dataSource);
        this.pgConnection = PgConnectionImpl.ofMaster(dataSource);
    }

    @Test
    void privateConstructor() {
        assertThrows(UnsupportedOperationException.class, () -> TestUtils.invokePrivateConstructor(QueryExecutor.class));
    }

    @Test
    void executeInvalidQuery() {
        final String invalidSql = "select unknown_field from unknown_table";
        assertThrows(RuntimeException.class, () -> QueryExecutor.executeQuery(
                pgConnection, invalidSql, (rs) -> null));
    }

    @Test
    void executeInvalidQueryWithSchema() {
        final String invalidSqlWithParam = "select unknown_field from unknown_table where schema = ?::text";
        assertThrows(RuntimeException.class, () -> QueryExecutor.executeQueryWithSchema(
                pgConnection, PgContext.of("s"), invalidSqlWithParam, (rs) -> null));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void executeNullQuery() {
        assertThrows(NullPointerException.class, () -> QueryExecutor.executeQuery(
                pgConnection, null, (rs) -> null));
    }
}
