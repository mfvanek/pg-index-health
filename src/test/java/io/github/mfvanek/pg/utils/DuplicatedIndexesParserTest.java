/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DuplicatedIndexesParserTest {

    @Test
    void privateConstructor() {
        assertThrows(UnsupportedOperationException.class, () -> TestUtils.invokePrivateConstructor(DuplicatedIndexesParser.class));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> parseAsIndexNameAndSize(null));
        assertThrows(IllegalArgumentException.class, () -> parseAsIndexNameAndSize(""));
        assertThrows(IllegalArgumentException.class, () -> parseAsIndexNameAndSize("  "));
    }

    @Test
    void withIncompleteString() {
        List<Map.Entry<String, Long>> entries = parseAsIndexNameAndSize("i");
        assertNotNull(entries);
        assertThat(entries, empty());

        entries = parseAsIndexNameAndSize("idx=i1, size=1");
        assertNotNull(entries);
        assertThat(entries, hasSize(1));

        entries = parseAsIndexNameAndSize("idx=i2,size=3");
        assertNotNull(entries);
        assertThat(entries, hasSize(1));
    }

    @Test
    void withSpaces() {
        final List<Map.Entry<String, Long>> entries = parseAsIndexNameAndSize("   idx=i2,   size=3   ; idx=i3   ,   size=5   ");
        assertNotNull(entries);
        assertThat(entries, hasSize(2));
    }

    @Test
    void withWrongDataFormat() {
        List<Map.Entry<String, Long>> entries = parseAsIndexNameAndSize("idx=i2, input=3");
        assertNotNull(entries);
        assertThat(entries, empty());

        entries = parseAsIndexNameAndSize("indx=i2, size=3");
        assertNotNull(entries);
        assertThat(entries, empty());
    }

    @Test
    void withWrongNumberFormat() {
        assertThrows(NumberFormatException.class, () -> parseAsIndexNameAndSize("idx=i2, size=AB3"));
    }
}
