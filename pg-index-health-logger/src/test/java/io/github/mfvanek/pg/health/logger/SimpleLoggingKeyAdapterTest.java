/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class SimpleLoggingKeyAdapterTest {

    @Test
    void conformityTest() {
        for (final Diagnostic diagnostic : Diagnostic.values()) {
            final LoggingKey key = SimpleLoggingKeyAdapter.of(diagnostic);
            assertThat(key.getKeyName())
                .isEqualTo("db_indexes_health");
            assertThat(key.getSubKeyName())
                .isEqualTo(diagnostic.name().toLowerCase(Locale.ROOT));
            assertThat(key.getDescription())
                .isEqualTo(diagnostic.name().toLowerCase(Locale.ROOT).replace('_', ' '));
        }
    }

    @Test
    void singleValue() {
        final LoggingKey key = SimpleLoggingKeyAdapter.of(Diagnostic.BTREE_INDEXES_ON_ARRAY_COLUMNS);
        assertThat(key.getSubKeyName())
            .isEqualTo("btree_indexes_on_array_columns");
        assertThat(key.getDescription())
            .isEqualTo("btree indexes on array columns");
    }
}
