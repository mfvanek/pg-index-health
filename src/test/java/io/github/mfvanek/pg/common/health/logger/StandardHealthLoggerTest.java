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

import io.github.mfvanek.pg.common.health.DatabaseHealth;
import io.github.mfvanek.pg.common.health.DatabaseHealthFactory;
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
    private final DatabaseHealth databaseHealth = Mockito.mock(DatabaseHealth.class);
    private final DatabaseHealthFactory databaseHealthFactory = Mockito.mock(DatabaseHealthFactory.class);
    private final HighAvailabilityPgConnectionFactory connectionFactory = Mockito.mock(HighAvailabilityPgConnectionFactory.class);
    private final HealthLogger logger = new StandardHealthLogger(credentials, connectionFactory, databaseHealthFactory);

    @BeforeEach
    void setUp() {
        Mockito.when(databaseHealthFactory.of(any())).thenReturn(databaseHealth);
    }

    @Test
    void logInvalidIndexes() {
        Mockito.when(databaseHealth.getInvalidIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        Index.of("t1", "i1"),
                        Index.of("t1", "i2"),
                        Index.of("t2", "i3")
                ));
        final List<String> logs = logger.logAll(Exclusions.empty());
        assertContainsKey(logs, SimpleLoggingKey.INVALID_INDEXES, "invalid_indexes:3");
    }
}
