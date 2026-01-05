/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneratingOptionsTest {

    @Test
    void testToString() {
        assertThat(GeneratingOptions.builder().build())
            .hasToString("GeneratingOptions{concurrently=true, excludeNulls=true, breakLines=true, indentation=4, uppercaseForKeywords=false, nameWithoutNulls=true, idxPosition=SUFFIX}");

        assertThat(GeneratingOptions.builder()
            .normally()
            .includeNulls()
            .doNotBreakLines()
            .withIndentation(2)
            .uppercaseForKeywords()
            .doNotNameWithoutNulls()
            .withIdxPosition(IdxPosition.PREFIX)
            .build())
            .hasToString("GeneratingOptions{concurrently=false, excludeNulls=false, breakLines=false, indentation=2, uppercaseForKeywords=true, nameWithoutNulls=false, idxPosition=PREFIX}");

        assertThat(GeneratingOptions.builder()
            .concurrently()
            .excludeNulls()
            .breakLines()
            .withIndentation(4)
            .lowercaseForKeywords()
            .nameWithoutNulls()
            .withIdxPosition(IdxPosition.SUFFIX)
            .build())
            .hasToString("GeneratingOptions{concurrently=true, excludeNulls=true, breakLines=true, indentation=4, uppercaseForKeywords=false, nameWithoutNulls=true, idxPosition=SUFFIX}");
    }

    @Test
    void cannotBuildTwice() {
        final GeneratingOptions.Builder builder = GeneratingOptions.builder();
        assertThatCode(builder::build)
            .as("First call")
            .doesNotThrowAnyException();
        assertThatThrownBy(builder::build)
            .as("Second call")
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("GeneratingOptions object has already been built");

        assertThatThrownBy(builder::concurrently)
            .as("Second call")
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("GeneratingOptions object has already been built");
    }

    @Test
    void normallyAndConcurrentlyShouldWork() {
        assertThat(GeneratingOptions.builder().build().isConcurrently())
            .isTrue();
        assertThat(GeneratingOptions.builder().normally().build().isConcurrently())
            .isFalse();
        assertThat(GeneratingOptions.builder().concurrently().build().isConcurrently())
            .isTrue();
    }

    @Test
    void includeNullsAndExcludeNullsShouldWork() {
        assertThat(GeneratingOptions.builder().build().isExcludeNulls())
            .isTrue();
        assertThat(GeneratingOptions.builder().includeNulls().build().isExcludeNulls())
            .isFalse();
        assertThat(GeneratingOptions.builder().excludeNulls().build().isExcludeNulls())
            .isTrue();
    }

    @Test
    void breakLinesShouldWork() {
        assertThat(GeneratingOptions.builder().build().isBreakLines())
            .isTrue();
        assertThat(GeneratingOptions.builder().doNotBreakLines().build().isBreakLines())
            .isFalse();
        assertThat(GeneratingOptions.builder().breakLines().build().isBreakLines())
            .isTrue();
    }

    @Test
    void withIndentationShouldWork() {
        assertThat(GeneratingOptions.builder().build().getIndentation())
            .isEqualTo(4);
        assertThat(GeneratingOptions.builder().withIndentation(2).build().getIndentation())
            .isEqualTo(2);
    }

    @Test
    void caseForKeywordsShouldWork() {
        assertThat(GeneratingOptions.builder().build().isUppercaseForKeywords())
            .isFalse();
        assertThat(GeneratingOptions.builder().uppercaseForKeywords().build().isUppercaseForKeywords())
            .isTrue();
        assertThat(GeneratingOptions.builder().lowercaseForKeywords().build().isUppercaseForKeywords())
            .isFalse();
    }

    @Test
    void nameWithoutNullsShouldWork() {
        assertThat(GeneratingOptions.builder().build().isNameWithoutNulls())
            .isTrue();
        assertThat(GeneratingOptions.builder().doNotNameWithoutNulls().build().isNameWithoutNulls())
            .isFalse();
        assertThat(GeneratingOptions.builder().nameWithoutNulls().build().isNameWithoutNulls())
            .isTrue();
    }

    @Test
    void withIdxPositionShouldWork() {
        assertThat(GeneratingOptions.builder().build())
            .satisfies(o -> {
                assertThat(o.getIdxPosition())
                    .isEqualTo(IdxPosition.SUFFIX);
                assertThat(o.isNeedToAddIdx())
                    .isTrue();
            });

        assertThat(GeneratingOptions.builder().withIdxPosition(IdxPosition.NONE).build())
            .satisfies(o -> {
                assertThat(o.getIdxPosition())
                    .isEqualTo(IdxPosition.NONE);
                assertThat(o.isNeedToAddIdx())
                    .isFalse();
            });
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

    @Test
    void withValidIndentation() {
        assertThat(GeneratingOptions.builder().withIndentation(0).build())
            .isNotNull()
            .satisfies(b -> assertThat(b.getIndentation()).isZero());

        assertThat(GeneratingOptions.builder().withIndentation(8).build())
            .isNotNull()
            .satisfies(b -> assertThat(b.getIndentation()).isEqualTo(8));
    }
}
