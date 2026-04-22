/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.generated;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModuleVersionTest {

    @Test
    void shouldReturnCorrectModuleVersion() {
        assertThat(ModuleVersion.VERSION)
            .isNotNull();
        assertThat(new ModuleVersion().version())
            .isEqualTo(ModuleVersion.VERSION);
    }
}
