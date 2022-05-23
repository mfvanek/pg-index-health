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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import javax.annotation.Nonnull;

import static io.github.mfvanek.pg.generator.PgIdentifierNameGeneratorTest.notNullColumnWithSchema;
import static io.github.mfvanek.pg.generator.PgIdentifierNameGeneratorTest.nullableColumnWithSchema;
import static org.assertj.core.api.Assertions.assertThat;

class PgIndexOnForeignKeyGeneratorTest {

    @Test
    void generateForSingleNotNullColumn() {
        final PgIndexOnForeignKeyGenerator generator = PgIndexOnForeignKeyGenerator.of(notNullColumnWithSchema(), GeneratingOptions.builder().build());
        assertThat(generator.generate())
                .isEqualTo("/* table_with_very_very_very_long_name_column_with_very_very_very_long_name_idx */\n" +
                        "create index concurrently if not exists table_with_very_very_very_long_name_3202677_idx\n" +
                        "    on schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name);");
    }

    @Test
    void generateForSingleNotNullColumnNormallyAndInSingleLine() {
        final PgIndexOnForeignKeyGenerator generator = PgIndexOnForeignKeyGenerator.of(notNullColumnWithSchema(),
                GeneratingOptions.builder().normally().doNotBreakLines().build());
        assertThat(generator.generate())
                .isEqualTo("/* table_with_very_very_very_long_name_column_with_very_very_very_long_name_idx */ " +
                        "create index if not exists table_with_very_very_very_long_name_3202677_idx " +
                        "on schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name);");
    }

    @Test
    void generateForSingleNotNullColumnWithUppercase() {
        final PgIndexOnForeignKeyGenerator generator = PgIndexOnForeignKeyGenerator.of(notNullColumnWithSchema(),
                GeneratingOptions.builder().uppercaseForKeywords().build());
        assertThat(generator.generate())
                .isEqualTo("/* table_with_very_very_very_long_name_column_with_very_very_very_long_name_idx */\n" +
                        "CREATE INDEX CONCURRENTLY IF NOT EXISTS table_with_very_very_very_long_name_3202677_idx\n" +
                        "    ON schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name);");
    }

    @Test
    void generateForSingleNullableColumn() {
        final PgIndexOnForeignKeyGenerator generator = PgIndexOnForeignKeyGenerator.of(nullableColumnWithSchema(), GeneratingOptions.builder().build());
        assertThat(generator.generate())
                .isEqualTo("/* table_with_very_very_very_long_name_column_with_very_very_very_long_name_without_nulls_idx */\n" +
                        "create index concurrently if not exists table_with_very_very_very_long_name_3202677_without_nulls_idx\n" +
                        "    on schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name) " +
                        "where column_with_very_very_very_long_name is not null;");
    }

    @Test
    void generateForSingleNullableColumnWithUppercase() {
        final PgIndexOnForeignKeyGenerator generator = PgIndexOnForeignKeyGenerator.of(nullableColumnWithSchema(),
                GeneratingOptions.builder().uppercaseForKeywords().build());
        assertThat(generator.generate())
                .isEqualTo("/* table_with_very_very_very_long_name_column_with_very_very_very_long_name_without_nulls_idx */\n" +
                        "CREATE INDEX CONCURRENTLY IF NOT EXISTS table_with_very_very_very_long_name_3202677_without_nulls_idx\n" +
                        "    ON schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name) " +
                        "WHERE column_with_very_very_very_long_name IS NOT NULL;");
    }

    @Test
    void generateForSingleNullableColumnWithNulls() {
        final PgIndexOnForeignKeyGenerator generator = PgIndexOnForeignKeyGenerator.of(nullableColumnWithSchema(),
                GeneratingOptions.builder().includeNulls().build());
        assertThat(generator.generate())
                .isEqualTo("/* table_with_very_very_very_long_name_column_with_very_very_very_long_name_idx */\n" +
                        "create index concurrently if not exists table_with_very_very_very_long_name_3202677_idx\n" +
                        "    on schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name);");
    }

    @Test
    void generateWithoutTruncation() {
        final PgIndexOnForeignKeyGenerator generator = PgIndexOnForeignKeyGenerator.of(severalColumnsWithNulls(), GeneratingOptions.builder().build());
        assertThat(generator.generate())
                .isEqualTo("create index concurrently if not exists table_column_1_column_2_without_nulls_idx\n" +
                        "    on table (column_1, column_2) where column_2 is not null;");
    }

    @Nonnull
    static ForeignKey severalColumnsWithNulls() {
        return ForeignKey.of("table", "cn",
                Arrays.asList(
                        Column.ofNotNull("table", "column_1"),
                        Column.ofNullable("table", "column_2")
                ));
    }
}
