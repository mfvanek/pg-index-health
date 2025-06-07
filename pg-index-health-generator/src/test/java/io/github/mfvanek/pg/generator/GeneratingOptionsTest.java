/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import org.junit.jupiter.api.Test;

import static io.github.mfvanek.pg.generator.GeneratingOptions.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneratingOptionsTest {

    @Test
    void testToString() {
        assertThat(builder().build())
            .hasToString("GeneratingOptions{concurrently=true, excludeNulls=true, breakLines=true, indentation=4, uppercaseForKeywords=false, nameWithoutNulls=true, idxPosition=SUFFIX}");

        assertThat(builder()
            .normally()
            .includeNulls()
            .doNotBreakLines()
            .withIndentation(2)
            .uppercaseForKeywords()
            .doNotNameWithoutNulls()
            .withIdxPosition(IdxPosition.PREFIX)
            .build())
            .hasToString("GeneratingOptions{concurrently=false, excludeNulls=false, breakLines=false, indentation=2, uppercaseForKeywords=true, nameWithoutNulls=false, idxPosition=PREFIX}");

        assertThat(builder()
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
        final GeneratingOptions.Builder builder = builder();
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
        assertThat(builder().build().isConcurrently())
            .isTrue();
        assertThat(builder().normally().build().isConcurrently())
            .isFalse();
        assertThat(builder().concurrently().build().isConcurrently())
            .isTrue();
    }

    @Test
    void includeNullsAndExcludeNullsShouldWork() {
        assertThat(builder().build().isExcludeNulls())
            .isTrue();
        assertThat(builder().includeNulls().build().isExcludeNulls())
            .isFalse();
        assertThat(builder().excludeNulls().build().isExcludeNulls())
            .isTrue();
    }

    @Test
    void breakLinesShouldWork() {
        assertThat(builder().build().isBreakLines())
            .isTrue();
        assertThat(builder().doNotBreakLines().build().isBreakLines())
            .isFalse();
        assertThat(builder().breakLines().build().isBreakLines())
            .isTrue();
    }

    @Test
    void withIndentationShouldWork() {
        assertThat(builder().build().getIndentation())
            .isEqualTo(4);
        assertThat(builder().withIndentation(2).build().getIndentation())
            .isEqualTo(2);
    }

    @Test
    void caseForKeywordsShouldWork() {
        assertThat(builder().build().isUppercaseForKeywords())
            .isFalse();
        assertThat(builder().uppercaseForKeywords().build().isUppercaseForKeywords())
            .isTrue();
        assertThat(builder().lowercaseForKeywords().build().isUppercaseForKeywords())
            .isFalse();
    }

    @Test
    void nameWithoutNullsShouldWork() {
        assertThat(builder().build().isNameWithoutNulls())
            .isTrue();
        assertThat(builder().doNotNameWithoutNulls().build().isNameWithoutNulls())
            .isFalse();
        assertThat(builder().nameWithoutNulls().build().isNameWithoutNulls())
            .isTrue();
    }

    @Test
    void withIdxPositionShouldWork() {
        assertThat(builder().build())
            .satisfies(o -> {
                assertThat(o.getIdxPosition())
                    .isEqualTo(IdxPosition.SUFFIX);
                assertThat(o.isNeedToAddIdx())
                    .isTrue();
            });

        assertThat(builder().withIdxPosition(IdxPosition.NONE).build())
            .satisfies(o -> {
                assertThat(o.getIdxPosition())
                    .isEqualTo(IdxPosition.NONE);
                assertThat(o.isNeedToAddIdx())
                    .isFalse();
            });
    }

    @Test
    void withInvalidArguments() {
        final GeneratingOptions.Builder builder = builder();

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
        assertThat(builder().withIndentation(0).build())
            .isNotNull()
            .satisfies(b -> assertThat(b.getIndentation()).isZero());

        assertThat(builder().withIndentation(8).build())
            .isNotNull()
            .satisfies(b -> assertThat(b.getIndentation()).isEqualTo(8));
    }
}
