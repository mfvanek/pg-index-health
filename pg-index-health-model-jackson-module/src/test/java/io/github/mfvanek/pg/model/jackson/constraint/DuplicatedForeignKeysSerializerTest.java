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

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DuplicatedForeignKeysSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final DuplicatedForeignKeys original = DuplicatedForeignKeys.of(
            ForeignKey.ofNotNullColumn("t1", "c1", "col1"),
            ForeignKey.ofNotNullColumn("t1", "c2", "col1"));
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("""
                {"tableName":"t1","foreignKeys":[\
                {"constraint":{"tableName":"t1","constraintName":"c1","constraintType":"FOREIGN_KEY"},"columns":[{"tableName":"t1","columnName":"col1","notNull":true}]},\
                {"constraint":{"tableName":"t1","constraintName":"c2","constraintType":"FOREIGN_KEY"},"columns":[{"tableName":"t1","columnName":"col1","notNull":true}]}]}""");
        final DuplicatedForeignKeys restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), DuplicatedForeignKeys.class);
        assertThat(restored)
            .isEqualTo(original);
    }

    @Test
    void deserializationShouldThrowExceptionOnMissingFields() {
        assertThatThrownBy(() -> objectMapper.readValue("{}", DuplicatedForeignKeys.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: foreignKeys");
    }
}
