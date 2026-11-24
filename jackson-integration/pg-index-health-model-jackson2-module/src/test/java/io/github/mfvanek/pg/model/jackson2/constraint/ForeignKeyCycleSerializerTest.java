/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson2.constraint;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.constraint.ForeignKeyCycle;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.jackson2.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ForeignKeyCycleSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final PgContext ctx = PgContext.ofDefault();
        final ForeignKeyCycle original = ForeignKeyCycle.of(
            ForeignKey.ofBigint(ctx, "t1", "c1", "col1"),
            ForeignKey.ofNullableBigint(ctx, "t2", "c2", "col2"));
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("""
                {"foreignKeys":[\
                {"constraint":{"tableName":"t1","constraintName":"c1","constraintType":"FOREIGN_KEY"},"columns":[{"tableName":"t1","columnName":"col1","notNull":true,"columnType":"bigint"}]},\
                {"constraint":{"tableName":"t2","constraintName":"c2","constraintType":"FOREIGN_KEY"},"columns":[{"tableName":"t2","columnName":"col2","notNull":false,"columnType":"bigint"}]}]}""");
        final ForeignKeyCycle restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), ForeignKeyCycle.class);
        assertThat(restored)
            .isEqualTo(original);
    }

    @Test
    void deserializationShouldThrowExceptionOnMissingFields() {
        assertThatThrownBy(() -> objectMapper.readValue("{}", ForeignKeyCycle.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: foreignKeys");
    }
}
