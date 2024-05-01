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
        assertThat(ConstraintType.fromConstraintType("c"))
                .isEqualTo(ConstraintType.CHECK);
        assertThat(ConstraintType.fromConstraintType("f"))
                .isEqualTo(ConstraintType.FOREIGN_KEY);

        assertThatThrownBy(() -> ConstraintType.fromConstraintType("hi"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown pgConType: hi");
    }
}
