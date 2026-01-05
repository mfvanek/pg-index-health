/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.settings.validation;

import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParamValidatorsTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(ParamValidators.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void paramValueNotNullShouldTrimWhitespaces() {
        assertThat(ParamValidators.paramValueNotNull("value1", "message"))
            .isEqualTo("value1");
        assertThat(ParamValidators.paramValueNotNull("  value2  ", "message"))
            .isEqualTo("value2");
        assertThat(ParamValidators.paramValueNotNull("  ", "message"))
            .isEmpty();
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void paramValueNotNullShouldThrowExceptionWhenNullValuePassed() {
        assertThatThrownBy(() -> ParamValidators.paramValueNotNull(null, "message"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("message");
    }
}
