/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConstraintTypeTest {

    @Test
    void valueFrom() {
        assertThat(ConstraintType.valueFrom("c"))
                .isEqualTo(ConstraintType.CHECK);
        assertThat(ConstraintType.valueFrom("f"))
                .isEqualTo(ConstraintType.FOREIGN_KEY);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void creationFromStringShouldThrowExceptionWhenNotFound() {
        assertThatThrownBy(() -> ConstraintType.valueFrom(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("pgConType cannot be null");
        assertThatThrownBy(() -> ConstraintType.valueFrom("hi"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown pgConType: hi");
    }
}
