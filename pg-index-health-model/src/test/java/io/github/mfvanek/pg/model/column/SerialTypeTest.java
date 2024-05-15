/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SerialTypeTest {

    @Test
    void allPgTypeNamesShouldBeUnique() {
        final Set<String> types = new HashSet<>();
        for (final SerialType serialType : SerialType.values()) {
            assertThat(serialType.getColumnType())
                .isNotBlank();
            types.add(serialType.getColumnType());
        }
        assertThat(types)
            .hasSameSizeAs(SerialType.values());
    }

    @Test
    void toStringTest() {
        assertThat(SerialType.SMALL_SERIAL)
            .hasToString("SerialType{columnType='smallserial'}");
        assertThat(SerialType.SERIAL)
            .hasToString("SerialType{columnType='serial'}");
        assertThat(SerialType.BIG_SERIAL)
            .hasToString("SerialType{columnType='bigserial'}");
    }

    @Test
    void creationFromStringShouldWork() {
        assertThat(SerialType.valueFrom("smallserial"))
            .isEqualTo(SerialType.SMALL_SERIAL);
        assertThat(SerialType.valueOf("SMALL_SERIAL"))
            .isEqualTo(SerialType.SMALL_SERIAL);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void creationFromStringShouldThrowExceptionWhenNotFound() {
        assertThatThrownBy(() -> SerialType.valueFrom(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgColumnType cannot be null");
        assertThatThrownBy(() -> SerialType.valueFrom(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgColumnType = ''");

        assertThatThrownBy(() -> SerialType.valueOf(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Name is null");
        assertThatThrownBy(() -> SerialType.valueOf(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("No enum constant io.github.mfvanek.pg.model.column.SerialType.");
    }
}
