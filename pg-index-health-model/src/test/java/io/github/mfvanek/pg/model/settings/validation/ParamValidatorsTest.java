/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.settings.validation;

import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParamValidatorsTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(ParamValidators.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
