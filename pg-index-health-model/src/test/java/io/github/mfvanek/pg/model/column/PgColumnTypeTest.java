/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgColumnTypeTest {

    @Test
    void allPgTypeNamesShouldBeUnique() {
        final Set<String> types = new HashSet<>();
        for (final PgColumnType serialType : PgColumnType.values()) {
            assertThat(serialType.getColumnType())
                .isNotBlank();
            types.add(serialType.getColumnType());
        }
        assertThat(types)
            .hasSameSizeAs(PgColumnType.values());
    }

    @Test
    void isSerialShouldWork() {
        assertThat(Arrays.stream(PgColumnType.values()).filter(PgColumnType::isSerial))
            .containsExactly(PgColumnType.SMALL_SERIAL, PgColumnType.SERIAL, PgColumnType.BIG_SERIAL);
    }

    @Test
    void toStringTest() {
        assertThat(PgColumnType.SMALL_SERIAL)
            .hasToString("PgColumnType{columnType='smallserial'}");
        assertThat(PgColumnType.SERIAL)
            .hasToString("PgColumnType{columnType='serial'}");
        assertThat(PgColumnType.BIG_SERIAL)
            .hasToString("PgColumnType{columnType='bigserial'}");
        assertThat(PgColumnType.VARCHAR)
            .hasToString("PgColumnType{columnType='character varying'}");
    }

    @Test
    void arrayTypeShouldWork() {
        assertThat(PgColumnType.TEXT.getArrayType())
            .isEqualTo("text[]");
        assertThat(PgColumnType.INTEGER.getArrayType())
            .isEqualTo("integer[]");
        assertThat(PgColumnType.TIMESTAMPTZ.getArrayType())
            .isEqualTo("timestamp with time zone[]");
    }

    @Test
    void arrayTypeShouldThrowExceptionForSerialTypes() {
        assertThatThrownBy(PgColumnType.SMALL_SERIAL::getArrayType)
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessage("Serial pseudo-type 'smallserial' has no array form");
        assertThatThrownBy(PgColumnType.SERIAL::getArrayType)
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessage("Serial pseudo-type 'serial' has no array form");
        assertThatThrownBy(PgColumnType.BIG_SERIAL::getArrayType)
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessage("Serial pseudo-type 'bigserial' has no array form");
    }

    @Test
    void creationFromStringShouldWork() {
        assertThat(PgColumnType.valueFrom("timestamp with time zone"))
            .isEqualTo(PgColumnType.TIMESTAMPTZ);
        assertThat(PgColumnType.valueFrom("smallserial"))
            .isEqualTo(PgColumnType.SMALL_SERIAL);
        assertThat(PgColumnType.valueOf("SMALL_SERIAL"))
            .isEqualTo(PgColumnType.SMALL_SERIAL);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void creationFromStringShouldThrowExceptionWhenNotFound() {
        assertThatThrownBy(() -> PgColumnType.valueFrom(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgColumnType cannot be null");
        assertThatThrownBy(() -> PgColumnType.valueFrom(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgColumnType = ''");

        assertThatThrownBy(() -> PgColumnType.valueOf(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Name is null");
        assertThatThrownBy(() -> PgColumnType.valueOf(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("No enum constant io.github.mfvanek.pg.model.column.PgColumnType.");
    }
}
