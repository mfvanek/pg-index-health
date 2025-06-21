/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.fixtures.support.LogsCaptor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class PgConnectionFactoryImplTest {

    private final PgConnectionFactory pgConnectionFactory = new PgConnectionFactoryImpl();

    @Test
    void forUrlTest() {
        try (LogsCaptor ignored = new LogsCaptor(PgConnectionFactoryImpl.class, Level.FINEST)) {
            final PgConnection pgConnection = pgConnectionFactory.forUrl(
                "jdbc:postgresql://localhost:5432/postgres?prepareThreshold=0&preparedStatementCacheQueries=0", "postgres", "postgres");
            assertThat(pgConnection)
                .isNotNull()
                .extracting(c -> c.getHost().getPgUrl())
                .isEqualTo("jdbc:postgresql://localhost:5432/postgres?prepareThreshold=0&preparedStatementCacheQueries=0");
        }
    }
}
