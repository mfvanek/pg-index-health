/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.dbobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgObjectTypeTest {

    @Test
    void valueFrom() {
        assertThat(PgObjectType.valueFrom("table"))
            .isEqualTo(PgObjectType.TABLE);
        assertThat(PgObjectType.valueFrom("INDEX"))
            .isEqualTo(PgObjectType.INDEX);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void creationFromStringShouldThrowExceptionWhenNotFound() {
        assertThatThrownBy(() -> PgObjectType.valueFrom(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("objectType cannot be null");
        assertThatThrownBy(() -> PgObjectType.valueFrom("hi"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown objectType: hi");
    }
}
