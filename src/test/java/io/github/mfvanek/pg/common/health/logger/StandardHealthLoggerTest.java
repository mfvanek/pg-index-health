/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheck;
import io.github.mfvanek.pg.common.maintenance.DatabaseChecks;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactory;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.Index;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static io.github.mfvanek.pg.utils.HealthLoggerAssertions.assertContainsKey;
import static org.mockito.ArgumentMatchers.any;

class StandardHealthLoggerTest {

    private final ConnectionCredentials credentials = Mockito.mock(ConnectionCredentials.class);
    private final HighAvailabilityPgConnectionFactory connectionFactory = Mockito.mock(HighAvailabilityPgConnectionFactory.class);
    private final DatabaseChecks databaseChecks = Mockito.mock(DatabaseChecks.class);
    @SuppressWarnings("unchecked")
    private final DatabaseCheck<Index> check = (DatabaseCheck<Index>) Mockito.mock(DatabaseCheck.class);
    private final HealthLogger logger = new StandardHealthLogger(credentials, connectionFactory, haPgConnection -> databaseChecks);

    @BeforeEach
    void setUp() {
        Mockito.when(databaseChecks.getCheck(Diagnostic.INVALID_INDEXES, Index.class)).thenReturn(check);
    }

    @Test
    void logInvalidIndexes() {
        Mockito.when(check.check(any(PgContext.class), any()))
                .thenReturn(Arrays.asList(
                        Index.of("t1", "i1"),
                        Index.of("t1", "i2"),
                        Index.of("t2", "i3")
                ));
        final List<String> logs = logger.logAll(Exclusions.empty());
        assertContainsKey(logs, SimpleLoggingKey.INVALID_INDEXES, "invalid_indexes:3");
    }
}
