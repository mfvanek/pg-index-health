/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.constraint;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ForeignKeySerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final ForeignKey original = ForeignKey.ofNotNullColumn("demo.orders", "client_id_fk", "client_id");
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("""
                {"constraint":{"tableName":"demo.orders","constraintName":"client_id_fk","constraintType":"FOREIGN_KEY"},\
                "columns":[{"tableName":"demo.orders","columnName":"client_id","notNull":true}]}""");
        final ForeignKey restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), ForeignKey.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }

    @Test
    void deserializationShouldThrowExceptionOnMissingFields() {
        assertThatThrownBy(() -> objectMapper.readValue("{}", ForeignKey.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: constraint");
        assertThatThrownBy(() -> objectMapper.readValue("""
            {"constraint":{"tableName":"demo.orders","constraintName":"client_id_fk","constraintType":"FOREIGN_KEY"}}""", ForeignKey.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: columns");
        assertThatThrownBy(() -> objectMapper.readValue("""
            {"constraint":{"tableName":"demo.orders","constraintName":"client_id_fk","constraintType":"FOREIGN_KEY"},\
            "columns":null}""", ForeignKey.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: columns");
    }
}
