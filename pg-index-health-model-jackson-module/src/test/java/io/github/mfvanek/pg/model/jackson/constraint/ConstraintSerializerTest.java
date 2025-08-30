/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.constraint;

import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ConstraintType;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ConstraintSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final Constraint original = Constraint.ofType("demo.orders", "order_amount_check", ConstraintType.CHECK);
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"tableName\":\"demo.orders\",\"constraintName\":\"order_amount_check\",\"constraintType\":\"CHECK\"}");
        final Constraint restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), Constraint.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }
}
