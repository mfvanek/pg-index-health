/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import com.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import com.mfvanek.pg.connection.PgConnectionImpl;
import com.mfvanek.pg.index.health.IndexesHealthImpl;
import com.mfvanek.pg.index.maintenance.MaintenanceFactoryImpl;
import com.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import static com.mfvanek.pg.utils.HealthLoggerAssertions.assertContainsKey;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class IndexesHealthLoggerTestBase extends DatabaseAwareTestBase {

    private final IndexesHealthLogger logger;

    IndexesHealthLoggerTestBase(@Nonnull final DataSource dataSource) {
        super(dataSource);
        final var haPgConnection = HighAvailabilityPgConnectionImpl.of(PgConnectionImpl.ofMaster(dataSource));
        final var indexesHealth = new IndexesHealthImpl(haPgConnection, new MaintenanceFactoryImpl());
        this.logger = new SimpleHealthLogger(indexesHealth);
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void logAll(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences()
                        .withData()
                        .withInvalidIndex()
                        .withNullValuesInIndex()
                        .withTableWithoutPrimaryKey()
                        .withDuplicatedIndex()
                        .withNonSuitableIndex(),
                ctx -> {
                    final var logs = logger.logAll(Exclusions.empty(), ctx);
                    assertNotNull(logs);
                    assertThat(logs, hasSize(8));
                    assertContainsKey(logs, SimpleLoggingKey.INVALID_INDEXES, "invalid_indexes\t1");
                    assertContainsKey(logs, SimpleLoggingKey.DUPLICATED_INDEXES, "duplicated_indexes\t2");
                    assertContainsKey(logs, SimpleLoggingKey.FOREIGN_KEYS, "foreign_keys_without_index\t1");
                    assertContainsKey(logs, SimpleLoggingKey.TABLES_WITHOUT_PK, "tables_without_primary_key\t1");
                    assertContainsKey(logs, SimpleLoggingKey.INDEXES_WITH_NULLS, "indexes_with_null_values\t1");
                });
    }

    @Test
    void logAllWithDefaultSchema() {
        final var logs = logger.logAll(Exclusions.empty());
        assertNotNull(logs);
        assertThat(logs, hasSize(8));
        for (var key : SimpleLoggingKey.values()) {
            assertContainsKey(logs, key, key.getSubKeyName() + "\t0");
        }
    }
}
