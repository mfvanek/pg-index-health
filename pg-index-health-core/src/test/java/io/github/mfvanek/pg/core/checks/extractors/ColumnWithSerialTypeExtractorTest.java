/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.extractors;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class ColumnWithSerialTypeExtractorTest {

    @Test
    void shouldCreateInstance() {
        assertThat(ColumnWithSerialTypeExtractor.of())
            .isNotNull()
            .isInstanceOf(ColumnWithSerialTypeExtractor.class);
    }
}
