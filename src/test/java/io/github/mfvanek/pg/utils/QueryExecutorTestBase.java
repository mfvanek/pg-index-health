/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
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
    void executeInvalidQuery() {
        final String invalidSql = "select unknown_field from unknown_table";
        assertThrows(RuntimeException.class, () -> QueryExecutor.executeQuery(
                pgConnection, invalidSql, (rs) -> null));

        final String invalidSqlWithParam = "select unknown_field from unknown_table where schema = ?::text";
        assertThrows(RuntimeException.class, () -> QueryExecutor.executeQuery(
                pgConnection, PgContext.of("s"), invalidSqlWithParam, (rs) -> null));
    }
}
