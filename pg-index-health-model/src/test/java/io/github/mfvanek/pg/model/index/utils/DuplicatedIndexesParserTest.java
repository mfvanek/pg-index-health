/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index.utils;

import io.github.mfvanek.pg.support.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DuplicatedIndexesParserTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(DuplicatedIndexesParser.class))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> DuplicatedIndexesParser.parseAsIndexNameAndSize(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("duplicatedAsString cannot be null");
        assertThatThrownBy(() -> DuplicatedIndexesParser.parseAsIndexNameAndSize(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duplicatedAsString cannot be blank");
        assertThatThrownBy(() -> DuplicatedIndexesParser.parseAsIndexNameAndSize("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duplicatedAsString cannot be blank");
    }

    @Test
    void withIncompleteString() {
        assertThat(DuplicatedIndexesParser.parseAsIndexNameAndSize("i"))
                .isUnmodifiable()
                .isEmpty();

        assertThat(DuplicatedIndexesParser.parseAsIndexNameAndSize("idx=i1, size=1"))
                .isUnmodifiable()
                .hasSize(1)
                .contains(Map.entry("i1", 1L));

        assertThat(DuplicatedIndexesParser.parseAsIndexNameAndSize("idx=i2,size=3"))
                .isUnmodifiable()
                .hasSize(1)
                .contains(Map.entry("i2", 3L));
    }

    @Test
    void withSpaces() {
        assertThat(DuplicatedIndexesParser.parseAsIndexNameAndSize("   idx=i2,   size=3   ; idx=i3   ,   size=5   "))
                .isUnmodifiable()
                .hasSize(2)
                .containsExactly(
                        Map.entry("i2", 3L),
                        Map.entry("i3", 5L));
    }

    @Test
    void withWrongDataFormat() {
        assertThat(DuplicatedIndexesParser.parseAsIndexNameAndSize("idx=i2, input=3"))
                .isUnmodifiable()
                .isEmpty();

        assertThat(DuplicatedIndexesParser.parseAsIndexNameAndSize("indx=i2, size=3"))
                .isUnmodifiable()
                .isEmpty();
    }

    @Test
    void withWrongNumberFormat() {
        assertThatThrownBy(() -> DuplicatedIndexesParser.parseAsIndexNameAndSize("idx=i2, size=AB3"))
                .isInstanceOf(NumberFormatException.class)
                .hasMessage("For input string: \"AB3\"");
    }
}
