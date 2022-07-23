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

import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.table.Column;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import javax.annotation.Nonnull;

import static io.github.mfvanek.pg.generator.PgIndexOnForeignKeyGenerator.MAX_IDENTIFIER_LENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class PgIdentifierNameGeneratorTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldThrowExceptionOnInvalidArguments() {
        assertThatThrownBy(() -> PgIdentifierNameGenerator.of(null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("foreignKey cannot be null");
        assertThatThrownBy(() -> PgIdentifierNameGenerator.of(ForeignKey.ofNullableColumn("t", "cn", "c"), null))
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

        final ForeignKey withNulls = nullableColumnWithSchema();
        assertThat(PgIdentifierNameGenerator.of(withNulls, GeneratingOptions.builder().build()).generateTruncatedIndexName())
                .isEqualTo("table_with_very_very_very_long_name_3202677_without_nulls_idx")
                .hasSizeLessThanOrEqualTo(MAX_IDENTIFIER_LENGTH);
        assertThat(PgIdentifierNameGenerator.of(withNulls,
                GeneratingOptions.builder().withIdxPosition(IdxPosition.PREFIX).build()).generateTruncatedIndexName())
                .isEqualTo("idx_table_with_very_very_very_long_name_3202677_without_nulls")
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
        final ForeignKey notNullColumn = ForeignKey.ofNotNullColumn("t", "cn", "col");
        PgIdentifierNameGenerator generator = PgIdentifierNameGenerator.of(notNullColumn, GeneratingOptions.builder().build());
        assertThat(generator.generateFullIndexName())
                .isEqualTo("t_col_idx")
                .isEqualTo(generator.generateTruncatedIndexName());

        final ForeignKey nullableColumn = ForeignKey.ofNullableColumn("t2", "cn2", "col2");
        generator = PgIdentifierNameGenerator.of(nullableColumn, GeneratingOptions.builder().build());
        assertThat(generator.generateFullIndexName())
                .isEqualTo("t2_col2_without_nulls_idx")
                .isEqualTo(generator.generateTruncatedIndexName());
    }

    @Nonnull
    static ForeignKey notNullColumnWithSchema() {
        return ForeignKey.ofNotNullColumn("schema_name_that_should_be_omitted.table_with_very_very_very_long_name",
                "cn", "column_with_very_very_very_long_name");
    }

    @Nonnull
    static ForeignKey nullableColumnWithSchema() {
        return ForeignKey.ofNullableColumn("schema_name_that_should_be_omitted.table_with_very_very_very_long_name",
                "cn", "column_with_very_very_very_long_name");
    }

    @Nonnull
    static ForeignKey severalColumnsWithoutNulls() {
        return ForeignKey.of("table_with_very_very_very_long_name", "cn",
                Arrays.asList(
                        Column.ofNotNull("table_with_very_very_very_long_name", "column_1_with_very_long_name"),
                        Column.ofNotNull("table_with_very_very_very_long_name", "column_2_with_very_long_name")
                ));
    }

    @Nonnull
    static ForeignKey severalColumnsWithNulls() {
        return ForeignKey.of("table_with_very_very_very_long_name", "cn",
                Arrays.asList(
                        Column.ofNotNull("table_with_very_very_very_long_name", "column_1_with_very_long_name"),
                        Column.ofNullable("table_with_very_very_very_long_name", "column_2_with_very_long_name")
                ));
    }
}
