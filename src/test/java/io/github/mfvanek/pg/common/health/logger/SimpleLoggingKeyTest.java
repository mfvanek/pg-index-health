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

import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleLoggingKeyTest {

    @Test
    void getKeyName() {
        final Set<String> keyNames = new HashSet<>();
        for (final LoggingKey key : SimpleLoggingKey.values()) {
            assertThat(key.getKeyName()).isNotNull();
            keyNames.add(key.getKeyName());
        }
        assertThat(keyNames).hasSize(1);
    }

    @Test
    void getSubKeyName() {
        final Set<String> subKeyNames = new HashSet<>();
        for (final LoggingKey key : SimpleLoggingKey.values()) {
            assertThat(key.getSubKeyName()).isNotNull();
            subKeyNames.add(key.getSubKeyName());
        }
        assertThat(subKeyNames).hasSize(SimpleLoggingKey.values().length);
    }

    @Test
    void completenessTest() {
        assertThat(SimpleLoggingKey.values())
                .as("There must be logging key for each diagnostic")
                .hasSameSizeAs(Diagnostic.values());
    }
}
