/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.github.mfvanek.pg.utils.DuplicatedIndexesParser.parseAsIndexNameAndSize;
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
        assertThatThrownBy(() -> parseAsIndexNameAndSize(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> parseAsIndexNameAndSize("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> parseAsIndexNameAndSize("  ")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void withIncompleteString() {
        List<Map.Entry<String, Long>> entries = parseAsIndexNameAndSize("i");
        assertThat(entries).isNotNull();
        assertThat(entries).isEmpty();

        entries = parseAsIndexNameAndSize("idx=i1, size=1");
        assertThat(entries).isNotNull();
        assertThat(entries).hasSize(1);

        entries = parseAsIndexNameAndSize("idx=i2,size=3");
        assertThat(entries).isNotNull();
        assertThat(entries).hasSize(1);
    }

    @Test
    void withSpaces() {
        final List<Map.Entry<String, Long>> entries = parseAsIndexNameAndSize("   idx=i2,   size=3   ; idx=i3   ,   size=5   ");
        assertThat(entries).isNotNull();
        assertThat(entries).hasSize(2);
    }

    @Test
    void withWrongDataFormat() {
        List<Map.Entry<String, Long>> entries = parseAsIndexNameAndSize("idx=i2, input=3");
        assertThat(entries).isNotNull();
        assertThat(entries).isEmpty();

        entries = parseAsIndexNameAndSize("indx=i2, size=3");
        assertThat(entries).isNotNull();
        assertThat(entries).isEmpty();
    }

    @Test
    void withWrongNumberFormat() {
        assertThatThrownBy(() -> parseAsIndexNameAndSize("idx=i2, size=AB3")).isInstanceOf(NumberFormatException.class);
    }
}
