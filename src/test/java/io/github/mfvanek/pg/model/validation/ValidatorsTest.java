/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.validation;

import io.github.mfvanek.pg.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class ValidatorsTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(Validators.class))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void valueIsPositive() {
        assertThat(Validators.valueIsPositive(1L, "arg"))
                .isEqualTo(1L);
        assertThatThrownBy(() -> Validators.valueIsPositive(0, "arg"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("arg should be greater than zero");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void indexNameNotBlank() {
        assertThat(Validators.indexNameNotBlank("idx"))
                .isEqualTo("idx");
        assertThatThrownBy(() -> Validators.indexNameNotBlank(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("indexName cannot be null");
        assertThatThrownBy(() -> Validators.indexNameNotBlank(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("indexName cannot be blank");
    }
}
