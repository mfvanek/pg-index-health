/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimpleLoggingKeyTest {

    @Test
    void getKeyName() {
        final Set<String> keyNames = new HashSet<>();
        for (LoggingKey key : SimpleLoggingKey.values()) {
            assertNotNull(key.getKeyName());
            keyNames.add(key.getKeyName());
        }
        assertThat(keyNames, hasSize(1));
    }

    @Test
    void getSubKeyName() {
        final Set<String> subKeyNames = new HashSet<>();
        for (LoggingKey key : SimpleLoggingKey.values()) {
            assertNotNull(key.getSubKeyName());
            subKeyNames.add(key.getSubKeyName());
        }
        assertThat(subKeyNames, hasSize(SimpleLoggingKey.values().length));
    }
}
