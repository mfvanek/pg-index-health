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

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.mfvanek.pg.generator.PgIndexOnForeignKeyGenerator.MAX_IDENTIFIER_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgIdentifierNameGeneratorTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldThrowExceptionOnInvalidArguments() {
        final PgContext ctx = PgContext.ofDefault();
        assertThatThrownBy(() -> PgIdentifierNameGenerator.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("foreignKey cannot be null");
        final ForeignKey foreignKey = ForeignKey.ofNullableBigint(ctx, "t", "cn", "c");
        assertThatThrownBy(() -> PgIdentifierNameGenerator.of(foreignKey, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("options cannot be null");
    }

    @Test
    void shouldGenerateFullIndexName() {
        final ForeignKey withoutNulls = notNullColumnWithSchema();
        assertThat(PgIdentifierNameGenerator.of(withoutNulls, GeneratingOptions.builder().build()).generateFullIndexName())
            .isEqualTo("table_with_very_very_very_long_name_column_with_very_very_very_long_name_idx")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(withoutNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).build()).generateFullIndexName())
            .isEqualTo("idx_table_with_very_very_very_long_name_column_with_very_very_very_long_name")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);

        final ForeignKey withNulls = nullableColumnWithSchema();
        assertThat(PgIdentifierNameGenerator.of(withNulls, GeneratingOptions.builder().build()).generateFullIndexName())
            .isEqualTo("table_with_very_very_very_long_name_column_with_very_very_very_long_name_without_nulls_idx")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(withNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).build()).generateFullIndexName())
            .isEqualTo("idx_table_with_very_very_very_long_name_column_with_very_very_very_long_name_without_nulls")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);

        final ForeignKey severalColumnsWithoutNulls = severalColumnsWithoutNulls();
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithoutNulls, GeneratingOptions.builder().build()).generateFullIndexName())
            .isEqualTo("table_with_very_very_very_long_name_column_1_with_very_long_name_column_2_with_very_long_name_idx")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithoutNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).build()).generateFullIndexName())
            .isEqualTo("idx_table_with_very_very_very_long_name_column_1_with_very_long_name_column_2_with_very_long_name")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);

        final ForeignKey severalColumnsWithNulls = severalColumnsWithNulls();
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls, GeneratingOptions.builder().build()).generateFullIndexName())
            .isEqualTo("table_with_very_very_very_long_name_column_1_with_very_long_name_column_2_with_very_long_name_without_nulls_idx")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).build()).generateFullIndexName())
            .isEqualTo("idx_table_with_very_very_very_long_name_column_1_with_very_long_name_column_2_with_very_long_name_without_nulls")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls, GeneratingOptions.builder().doNotNameWithoutNulls().build()).generateFullIndexName())
            .isEqualTo("table_with_very_very_very_long_name_column_1_with_very_long_name_column_2_with_very_long_name_idx")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).doNotNameWithoutNulls().build()).generateFullIndexName())
            .isEqualTo("idx_table_with_very_very_very_long_name_column_1_with_very_long_name_column_2_with_very_long_name")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);
    }

    @Test
    void shouldGenerateFullNameWithoutIdx() {
        final ForeignKey severalColumnsWithoutNulls = severalColumnsWithoutNulls();
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithoutNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.NONE).build()).generateFullIndexName())
            .isEqualTo("table_with_very_very_very_long_name_column_1_with_very_long_name_column_2_with_very_long_name")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithoutNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.NONE).includeNulls().build()).generateFullIndexName())
            .isEqualTo("table_with_very_very_very_long_name_column_1_with_very_long_name_column_2_with_very_long_name")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);

        final ForeignKey severalColumnsWithNulls = severalColumnsWithNulls();
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.NONE).build()).generateFullIndexName())
            .isEqualTo("table_with_very_very_very_long_name_column_1_with_very_long_name_column_2_with_very_long_name_without_nulls")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.NONE).includeNulls().build()).generateFullIndexName())
            .isEqualTo("table_with_very_very_very_long_name_column_1_with_very_long_name_column_2_with_very_long_name")
            .hasSizeGreaterThan(MAX_IDENTIFIER_LENGTH);
    }

    @Test
    void shouldGenerateTruncatedIndexName() {
        final ForeignKey withoutNulls = notNullColumnWithSchema();
        assertThat(PgIdentifierNameGenerator.of(withoutNulls, GeneratingOptions.builder().build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_3202677_idx")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(withoutNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).build()).generateTruncatedIndexName())
            .isEqualTo("idx_table_with_very_very_very_long_name_3202677")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);

        final ForeignKey severalColumnsWithoutNulls = severalColumnsWithoutNulls();
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithoutNulls, GeneratingOptions.builder().build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_n1959284032_idx")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithoutNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).build()).generateTruncatedIndexName())
            .isEqualTo("idx_table_with_very_very_very_long_name_n1959284032")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);

        final ForeignKey severalColumnsWithNulls = severalColumnsWithNulls();
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls, GeneratingOptions.builder().build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_n1959284032_idx")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).build()).generateTruncatedIndexName())
            .isEqualTo("idx_table_with_very_very_very_long_name_n1959284032")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls, GeneratingOptions.builder().doNotNameWithoutNulls().build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_n1959284032_idx")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).doNotNameWithoutNulls().build()).generateTruncatedIndexName())
            .isEqualTo("idx_table_with_very_very_very_long_name_n1959284032")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
    }

    @Test
    void shouldGenerateTruncatedIndexNameWithoutNulls() {
        final PgContext ctx = PgContext.ofDefault();
        final ForeignKey first = nullableColumnWithSchema();
        assertThat(PgIdentifierNameGenerator.of(first, GeneratingOptions.builder().build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_3202677_without_nulls_idx")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(first,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).build()).generateTruncatedIndexName())
            .isEqualTo("idx_table_with_very_very_very_long_name_3202677_without_nulls")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);

        final ForeignKey second = ForeignKey.ofNullableBigint(ctx, "table_with_very_very_very_long_name_1", "cn", "column1");
        assertThat(PgIdentifierNameGenerator.of(second, GeneratingOptions.builder().build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_1_column1_without_nulls_idx")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);

        final ForeignKey third = ForeignKey.ofNullableBigint(ctx, "table_with_very_very_very_long_name_12", "cn", "column1");
        assertThat(PgIdentifierNameGenerator.of(third, GeneratingOptions.builder().build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_12_column1_idx")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
    }

    @Test
    void shouldGenerateTruncatedIndexNameForColumnWithZeroHash() {
        final ForeignKey columnWithZeroHash = notNullColumnWithZeroHash();
        assertThat(PgIdentifierNameGenerator.of(columnWithZeroHash, GeneratingOptions.builder().build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_that_is_even_longer_0_idx")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(columnWithZeroHash,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).build()).generateTruncatedIndexName())
            .isEqualTo("idx_table_with_very_very_very_long_name_that_is_even_longer_0")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
    }

    @Test
    void shouldGenerateTruncatedNameWithoutIdx() {
        final ForeignKey severalColumnsWithoutNulls = severalColumnsWithoutNulls();
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithoutNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.NONE).build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_n1959284032")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithoutNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.NONE).includeNulls().build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_n1959284032")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);

        final ForeignKey severalColumnsWithNulls = severalColumnsWithNulls();
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.NONE).build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_n1959284032_without_nulls")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(severalColumnsWithNulls,
            GeneratingOptions.builder().withIdxPosition(IdxPosition.NONE).includeNulls().build()).generateTruncatedIndexName())
            .isEqualTo("table_with_very_very_very_long_name_n1959284032")
            .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
    }

    @Test
    void fullAndTruncatedNamesShouldBeTheSameForShortCases() {
        final PgContext ctx = PgContext.ofDefault();
        final ForeignKey first = ForeignKey.ofBigint(ctx, "t", "cn", "col");
        PgIdentifierNameGenerator generator = PgIdentifierNameGenerator.of(first, GeneratingOptions.builder().build());
        assertThat(generator.generateFullIndexName())
            .isEqualTo("t_col_idx")
            .isEqualTo(generator.generateTruncatedIndexName());

        final ForeignKey second = ForeignKey.ofBigint(ctx, "t_1234567890_1234567890_1234567890_1234567890", "cn", "col_123456789");
        generator = PgIdentifierNameGenerator.of(second, GeneratingOptions.builder().build());
        assertThat(generator.generateFullIndexName())
            .isEqualTo("t_1234567890_1234567890_1234567890_1234567890_col_123456789_idx")
            .isEqualTo(generator.generateTruncatedIndexName());

        final ForeignKey third = ForeignKey.ofBigint(ctx, "t_1234567890_1234567890_1234567890_1234567890", "cn", "col_12345678");
        generator = PgIdentifierNameGenerator.of(third, GeneratingOptions.builder().build());
        assertThat(generator.generateFullIndexName())
            .isEqualTo("t_1234567890_1234567890_1234567890_1234567890_col_12345678_idx")
            .isEqualTo(generator.generateTruncatedIndexName());

        final ForeignKey nullableColumn = ForeignKey.ofNullableBigint(ctx, "t2", "cn2", "col2");
        generator = PgIdentifierNameGenerator.of(nullableColumn, GeneratingOptions.builder().build());
        assertThat(generator.generateFullIndexName())
            .isEqualTo("t2_col2_without_nulls_idx")
            .isEqualTo(generator.generateTruncatedIndexName());
    }

    @NonNull
    static ForeignKey notNullColumnWithSchema() {
        final PgContext ctx = PgContext.ofDefault();
        return ForeignKey.ofBigint(ctx, "schema_name_that_should_be_omitted.table_with_very_very_very_long_name",
            "cn", "column_with_very_very_very_long_name");
    }

    @NonNull
    static ForeignKey nullableColumnWithSchema() {
        final PgContext ctx = PgContext.ofDefault();
        return ForeignKey.ofNullableBigint(ctx, "schema_name_that_should_be_omitted.table_with_very_very_very_long_name",
            "cn", "column_with_very_very_very_long_name");
    }

    @NonNull
    static ForeignKey notNullColumnWithZeroHash() {
        final PgContext ctx = PgContext.ofDefault();
        return ForeignKey.ofBigint(ctx, "table_with_very_very_very_long_name_that_is_even_longer", "cn", "corrugatestendentiousness");
    }

    @NonNull
    static ForeignKey severalColumnsWithoutNulls() {
        final PgContext ctx = PgContext.ofDefault();
        return ForeignKey.of(ctx, "table_with_very_very_very_long_name", "cn",
            List.of(
                Column.ofBigint(ctx, "table_with_very_very_very_long_name", "column_1_with_very_long_name"),
                Column.ofBigint(ctx, "table_with_very_very_very_long_name", "column_2_with_very_long_name")
            ));
    }

    @NonNull
    static ForeignKey severalColumnsWithNulls() {
        final PgContext ctx = PgContext.ofDefault();
        return ForeignKey.of(ctx, "table_with_very_very_very_long_name", "cn",
            List.of(
                Column.ofBigint(ctx, "table_with_very_very_very_long_name", "column_1_with_very_long_name"),
                Column.ofNullableBigint(ctx, "table_with_very_very_very_long_name", "column_2_with_very_long_name")
            ));
    }
}
