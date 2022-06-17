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

import io.github.mfvanek.pg.common.maintenance.DatabaseChecks;
import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactoryImpl;
import io.github.mfvanek.pg.connection.PgConnectionFactoryImpl;
import io.github.mfvanek.pg.connection.PrimaryHostDeterminerImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.utils.HealthLoggerAssertions.assertContainsKey;
import static org.assertj.core.api.Assertions.assertThat;

class HealthLoggerTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final HealthLogger logger;

    HealthLoggerTest() {
        super(POSTGRES.getTestDatabase());
        final ConnectionCredentials credentials = ConnectionCredentials.ofUrl(
                POSTGRES.getUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        this.logger = new KeyValueFileHealthLogger(
                credentials,
                new HighAvailabilityPgConnectionFactoryImpl(new PgConnectionFactoryImpl(), new PrimaryHostDeterminerImpl()),
                DatabaseChecks::new);
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void logAll(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withInvalidIndex().withNullValuesInIndex().withTableWithoutPrimaryKey().withDuplicatedIndex().withNonSuitableIndex().withStatistics(), ctx -> {
                    waitForStatisticsCollector();
                    final List<String> logs = logger.logAll(Exclusions.empty(), ctx);
                    assertThat(logs)
                            .isNotNull()
                            .hasSize(10);
                    assertContainsKey(logs, SimpleLoggingKey.INVALID_INDEXES, "invalid_indexes\t1");
                    assertContainsKey(logs, SimpleLoggingKey.DUPLICATED_INDEXES, "duplicated_indexes\t2");
                    assertContainsKey(logs, SimpleLoggingKey.FOREIGN_KEYS, "foreign_keys_without_index\t1");
                    assertContainsKey(logs, SimpleLoggingKey.TABLES_WITHOUT_PK, "tables_without_primary_key\t1");
                    assertContainsKey(logs, SimpleLoggingKey.INDEXES_WITH_NULLS, "indexes_with_null_values\t1");
                    assertContainsKey(logs, SimpleLoggingKey.INDEXES_BLOAT, "indexes_bloat\t11");
                    assertContainsKey(logs, SimpleLoggingKey.TABLES_BLOAT, "tables_bloat\t2");
                });
    }

    @Test
    void logAllWithDefaultSchema() {
        final List<String> logs = logger.logAll(Exclusions.empty());
        assertThat(logs)
                .isNotNull()
                .hasSize(10);
        for (final SimpleLoggingKey key : SimpleLoggingKey.values()) {
            assertContainsKey(logs, key, key.getSubKeyName() + "\t0");
        }
    }
}
