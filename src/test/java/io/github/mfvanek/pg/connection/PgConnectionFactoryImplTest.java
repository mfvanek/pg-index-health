/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PgConnectionFactoryImplTest {

    private final PgConnectionFactory pgConnectionFactory = new PgConnectionFactoryImpl();

    @Test
    void forUrlTest() {
        final PgConnection pgConnection = pgConnectionFactory.forUrl(
                "jdbc:postgresql://localhost:5432/postgres?prepareThreshold=0&preparedStatementCacheQueries=0", "postgres", "postgres");
        assertThat(pgConnection).isNotNull();
        assertThat(pgConnection.getHost().getPgUrl()).isEqualTo("jdbc:postgresql://localhost:5432/postgres?prepareThreshold=0&preparedStatementCacheQueries=0");
    }
}
