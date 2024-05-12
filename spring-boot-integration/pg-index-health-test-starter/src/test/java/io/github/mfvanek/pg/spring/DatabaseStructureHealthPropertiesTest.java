/*
 * Copyright (c) 2021-2024. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health-test-starter
 *
 * This file is a part of "pg-index-health-test-starter".
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
