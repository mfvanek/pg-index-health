/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.PgContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class QueryExecutorTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    private final PgConnection pgConnection;

    QueryExecutorTest() {
        super(embeddedPostgres.getTestDatabase());
        this.pgConnection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
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
