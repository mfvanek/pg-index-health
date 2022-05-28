/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneratingOptionsTest {

    @Test
    void testToString() {
        final GeneratingOptions.Builder builder = GeneratingOptions.builder();
        final GeneratingOptions options = builder.build();
        assertThat(options)
                .hasToString("GeneratingOptions{concurrently=true, excludeNulls=true, breakLines=true, indentation=4, uppercaseForKeywords=false, nameWithoutNulls=true, idxPosition=SUFFIX}");

        builder.normally()
                .includeNulls()
                .doNotBreakLines()
                .withIndentation(2)
                .uppercaseForKeywords()
                .doNotNameWithoutNulls()
                .withIdxPosition(IdxPosition.PREFIX);
        assertThat(builder)
                .hasToString("Builder{concurrently=false, excludeNulls=false, breakLines=false, indentation=2, uppercaseForKeywords=true, nameWithoutNulls=false, idxPosition=PREFIX}");

        builder.concurrently()
                .excludeNulls()
                .breakLines()
                .withIndentation(4)
                .lowercaseForKeywords()
                .nameWithoutNulls()
                .withIdxPosition(IdxPosition.SUFFIX);
        assertThat(builder)
                .hasToString("Builder{concurrently=true, excludeNulls=true, breakLines=true, indentation=4, uppercaseForKeywords=false, nameWithoutNulls=true, idxPosition=SUFFIX}");
    }

    @Test
    void gettersAndBuilderShouldWork() {
        final GeneratingOptions.Builder builder = GeneratingOptions.builder();

        assertThat(builder.build().isConcurrently()).isTrue();
        builder.normally();
        assertThat(builder.build().isConcurrently()).isFalse();
        builder.concurrently();
        assertThat(builder.build().isConcurrently()).isTrue();

        assertThat(builder.build().isExcludeNulls()).isTrue();
        builder.includeNulls();
        assertThat(builder.build().isExcludeNulls()).isFalse();
        builder.excludeNulls();
        assertThat(builder.build().isExcludeNulls()).isTrue();

        assertThat(builder.build().isBreakLines()).isTrue();
        builder.doNotBreakLines();
        assertThat(builder.build().isBreakLines()).isFalse();
        builder.breakLines();
        assertThat(builder.build().isBreakLines()).isTrue();

        assertThat(builder.build().getIndentation()).isEqualTo(4);
        builder.withIndentation(2);
        assertThat(builder.build().getIndentation()).isEqualTo(2);

        assertThat(builder.build().isUppercaseForKeywords()).isFalse();
        builder.uppercaseForKeywords();
        assertThat(builder.build().isUppercaseForKeywords()).isTrue();
        builder.lowercaseForKeywords();
        assertThat(builder.build().isUppercaseForKeywords()).isFalse();

        assertThat(builder.build().isNameWithoutNulls()).isTrue();
        builder.doNotNameWithoutNulls();
        assertThat(builder.build().isNameWithoutNulls()).isFalse();
        builder.nameWithoutNulls();
        assertThat(builder.build().isNameWithoutNulls()).isTrue();

        assertThat(builder.build().getIdxPosition()).isEqualTo(IdxPosition.SUFFIX);
        assertThat(builder.build().isNeedToAddIdx()).isTrue();
        builder.withIdxPosition(IdxPosition.NONE);
        assertThat(builder.build().getIdxPosition()).isEqualTo(IdxPosition.NONE);
        assertThat(builder.build().isNeedToAddIdx()).isFalse();
    }

    @Test
    void withInvalidArguments() {
        final GeneratingOptions.Builder builder = GeneratingOptions.builder();

        assertThatThrownBy(() -> builder.withIndentation(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("indentation should be in the range [0, 8]");
        assertThatThrownBy(() -> builder.withIndentation(9))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("indentation should be in the range [0, 8]");

        //noinspection ConstantConditions
        assertThatThrownBy(() -> builder.withIdxPosition(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("idxPosition cannot be null");
    }
}
