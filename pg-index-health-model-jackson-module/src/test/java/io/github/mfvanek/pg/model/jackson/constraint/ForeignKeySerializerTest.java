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

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.ConstraintType;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class ForeignKeySerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final ForeignKey original = ForeignKey.ofNotNullColumn("demo.orders", "client_id_fk", "client_id");
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("""
                {"constraint":{"tableName":"demo.orders","constraintName":"client_id_fk","constraintType":"FOREIGN_KEY"},\
                "columnsInConstraint":[{"tableName":"demo.orders","columnName":"client_id","notNull":true}]}""");
        final ForeignKey restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), ForeignKey.class);
        assertThat(restored)
            .isEqualTo(original)
            .satisfies(c -> {
                assertThat(c.getConstraintType()).isEqualTo(ConstraintType.FOREIGN_KEY);
                assertThat(c.getColumns())
                    .hasSize(1)
                    .containsExactly(Column.ofNotNull("demo.orders", "client_id"));
            });
    }
}
