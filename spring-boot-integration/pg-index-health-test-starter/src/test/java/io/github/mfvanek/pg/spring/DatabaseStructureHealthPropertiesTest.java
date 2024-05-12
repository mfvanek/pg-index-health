/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseStructureHealthPropertiesTest {

    @Test
    void getterShouldWork() {
        final DatabaseStructureHealthProperties propertiesEnabled = new DatabaseStructureHealthProperties(true);
        assertThat(propertiesEnabled.isEnabled())
            .isTrue();
        assertThat(propertiesEnabled)
            .hasToString("DatabaseStructureHealthProperties{enabled=true}");

        final DatabaseStructureHealthProperties propertiesDisabled = new DatabaseStructureHealthProperties(false);
        assertThat(propertiesDisabled.isEnabled())
            .isFalse();
        assertThat(propertiesDisabled)
            .hasToString("DatabaseStructureHealthProperties{enabled=false}");
    }
}
