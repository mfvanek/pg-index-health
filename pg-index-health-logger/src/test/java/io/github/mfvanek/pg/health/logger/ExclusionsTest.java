/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.model.units.MemoryUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class ExclusionsTest {

    @Test
    void cannotBuildTwice() {
        final Exclusions.Builder builder = Exclusions.builder();
        assertThatCode(builder::build)
            .as("First call")
            .doesNotThrowAnyException();
        assertThatThrownBy(builder::build)
            .as("Second call")
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Exclusions object has already been built");

        assertThatThrownBy(() -> builder.withSequence("s1"))
            .as("Second call")
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Exclusions object has already been built");
    }

    @Test
    void sizeInBytesTest() {
        final Exclusions e = Exclusions.builder()
            .withIndexSizeThreshold(11L)
            .withTableSizeThreshold(22L)
            .withBloatSizeThreshold(33L)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isEqualTo(11L);
        assertThat(e.getTableSizeThresholdInBytes()).isEqualTo(22L);
        assertThat(e.getBloatSizeThresholdInBytes()).isEqualTo(33L);
    }

    @Test
    void withMemoryUnitTest() {
        final Exclusions e = Exclusions.builder()
            .withTableSizeThreshold(10, MemoryUnit.MB)
            .withIndexSizeThreshold(2, MemoryUnit.GB)
            .withBloatSizeThreshold(4, MemoryUnit.KB)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isEqualTo(2_147_483_648L);
        assertThat(e.getTableSizeThresholdInBytes()).isEqualTo(10_485_760L);
        assertThat(e.getBloatSizeThresholdInBytes()).isEqualTo(4_096L);
    }

    @Test
    void emptyTest() {
        final Exclusions e = Exclusions.empty();
        assertThat(e).isNotNull();

        assertThat(e.getIndexNameExclusions())
            .isUnmodifiable()
            .isEmpty();

        assertThat(e.getTableNameExclusions())
            .isUnmodifiable()
            .isEmpty();

        assertThat(e.getSequenceNameExclusions())
            .isUnmodifiable()
            .isEmpty();

        assertThat(e.getIndexSizeThresholdInBytes()).isZero();
        assertThat(e.getTableSizeThresholdInBytes()).isZero();
        assertThat(e.getBloatSizeThresholdInBytes()).isZero();
        assertThat(e.getBloatPercentageThreshold()).isZero();
    }

    @Test
    void toStringTest() {
        final Exclusions e = Exclusions.empty();
        assertThat(e)
            .hasToString("""
                Exclusions{indexNameExclusions=[], tableNameExclusions=[], sequenceNameExclusions=[], \
                indexSizeThresholdInBytes=0, tableSizeThresholdInBytes=0, bloatSizeThresholdInBytes=0, bloatPercentageThreshold=0.0}""");
    }

    @Test
    void builderWithZeroSizeInBytes() {
        final Exclusions e = Exclusions.builder()
            .withIndexSizeThreshold(0L)
            .withTableSizeThreshold(0L)
            .withBloatSizeThreshold(0L)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isZero();
        assertThat(e.getTableSizeThresholdInBytes()).isZero();
        assertThat(e.getBloatSizeThresholdInBytes()).isZero();
    }

    @Test
    void builderWithZeroSizeInMemoryUnits() {
        final Exclusions e = Exclusions.builder()
            .withIndexSizeThreshold(0, MemoryUnit.KB)
            .withTableSizeThreshold(0, MemoryUnit.KB)
            .withBloatSizeThreshold(0, MemoryUnit.KB)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isZero();
        assertThat(e.getTableSizeThresholdInBytes()).isZero();
        assertThat(e.getBloatSizeThresholdInBytes()).isZero();
    }

    @Test
    void builderWithInvalidSize() {
        final Exclusions.Builder builder = Exclusions.builder();
        assertThatThrownBy(() -> builder.withIndexSizeThreshold(-1L))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.withTableSizeThreshold(-1L))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.withBloatSizeThreshold(-1L))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.withIndexSizeThreshold(-1, MemoryUnit.KB))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.withTableSizeThreshold(-1, MemoryUnit.KB))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.withBloatSizeThreshold(-1, MemoryUnit.KB))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void zeroPercentageThreshold() {
        final Exclusions e = Exclusions.builder()
            .withBloatPercentageThreshold(0)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getBloatPercentageThreshold()).isZero();
    }

    @Test
    void maxPercentageThreshold() {
        final Exclusions e = Exclusions.builder()
            .withBloatPercentageThreshold(100)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getBloatPercentageThreshold()).isEqualTo(100);
    }

    @Test
    void invalidPercentageThreshold() {
        final Exclusions.Builder builder = Exclusions.builder();
        assertThatThrownBy(() -> builder.withBloatPercentageThreshold(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("bloatPercentageThreshold should be in the range from 0.0 to 100.0 inclusive");
        assertThatThrownBy(() -> builder.withBloatPercentageThreshold(101))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("bloatPercentageThreshold should be in the range from 0.0 to 100.0 inclusive");
    }

    @Test
    void withIndexesShouldWork() {
        final Exclusions e = Exclusions.builder()
            .withIndexes(Set.of("i1", "i2", "i3"))
            .withIndex("i3")
            .withIndex("i4")
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexNameExclusions())
            .hasSize(4)
            .containsExactlyInAnyOrder("i1", "i2", "i3", "i4");

        final Exclusions.Builder builder = Exclusions.builder();
        final Set<String> indexNameExclusions = Set.of("i1", "");
        assertThatThrownBy(() -> builder.withIndexes(indexNameExclusions))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("indexNameExclusions cannot be blank");
        assertThatThrownBy(() -> builder.withIndex("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("indexNameExclusions cannot be blank");
    }

    @Test
    void withTablesShouldWork() {
        final Exclusions e = Exclusions.builder()
            .withTables(Set.of("t1", "t2", "t3"))
            .withTable("t3")
            .withTable("t4")
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getTableNameExclusions())
            .hasSize(4)
            .containsExactlyInAnyOrder("t1", "t2", "t3", "t4");

        final Exclusions.Builder builder = Exclusions.builder();
        final Set<String> tableNameExclusions = Set.of("t1", "");
        assertThatThrownBy(() -> builder.withTables(tableNameExclusions))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableNameExclusions cannot be blank");
        assertThatThrownBy(() -> builder.withTable("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableNameExclusions cannot be blank");
    }

    @Test
    void withSequencesShouldWork() {
        final Exclusions e = Exclusions.builder()
            .withSequences(Set.of("s1", "s2", "s3"))
            .withSequence("s3")
            .withSequence("s4")
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getSequenceNameExclusions())
            .hasSize(4)
            .containsExactlyInAnyOrder("s1", "s2", "s3", "s4");

        final Exclusions.Builder builder = Exclusions.builder();
        final Set<String> sequenceNameExclusions = Set.of("t1", "");
        assertThatThrownBy(() -> builder.withSequences(sequenceNameExclusions))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("sequenceNameExclusions cannot be blank");
        assertThatThrownBy(() -> builder.withSequence("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("sequenceNameExclusions cannot be blank");
    }
}
