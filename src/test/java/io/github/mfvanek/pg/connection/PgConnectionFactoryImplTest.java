/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.connection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PgConnectionFactoryImplTest {

    private final PgConnectionFactory pgConnectionFactory = new PgConnectionFactoryImpl();

    @Test
    void forUrlTest() {
        final var pgConnection = pgConnectionFactory.forUrl(
                "jdbc:postgresql://localhost:5432/postgres?prepareThreshold=0&preparedStatementCacheQueries=0", "postgres", "postgres");
        assertNotNull(pgConnection);
        assertEquals("jdbc:postgresql://localhost:5432/postgres?prepareThreshold=0&preparedStatementCacheQueries=0",
                pgConnection.getHost().getPgUrl());
    }
}
