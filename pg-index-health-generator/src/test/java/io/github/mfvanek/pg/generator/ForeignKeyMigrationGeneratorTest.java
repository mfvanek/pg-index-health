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

import io.github.mfvanek.pg.generator.support.GeneratorTestBase;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.mfvanek.pg.generator.PgIdentifierNameGeneratorTest.nullableColumnWithSchema;
import static io.github.mfvanek.pg.generator.PgIndexOnForeignKeyGeneratorTest.severalColumnsWithNulls;
import static org.assertj.core.api.Assertions.assertThat;

class ForeignKeyMigrationGeneratorTest extends GeneratorTestBase {

    @Test
    void generateForSingleForeignKey() {
        final DbMigrationGenerator<ForeignKey> generator = new ForeignKeyMigrationGenerator(GeneratingOptions.builder().build());
        final List<String> result = generator.generate(List.of(nullableColumnWithSchema()));
        assertThat(result)
            .hasSize(1)
            .containsExactly(normalizeEndings("""
                /* table_with_very_very_very_long_name_column_with_very_very_very_long_name_without_nulls_idx */
                create index concurrently if not exists table_with_very_very_very_long_name_3202677_without_nulls_idx
                    on schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name) \
                where column_with_very_very_very_long_name is not null;"""));
    }

    @Test
    void generateForSeveralForeignKeys() {
        final DbMigrationGenerator<ForeignKey> generator = new ForeignKeyMigrationGenerator(GeneratingOptions.builder().build());
        final List<String> result = generator.generate(List.of(severalColumnsWithNulls(), severalColumnsWithNulls(), nullableColumnWithSchema()));
        assertThat(result)
            .hasSize(3)
            .containsExactly(
                normalizeEndings("""
                    create index concurrently if not exists custom_table_custom_column_1_custom_column_22_without_nulls_idx
                        on custom_table (custom_column_1, custom_column_22) where custom_column_22 is not null;"""),
                normalizeEndings("""
                    create index concurrently if not exists custom_table_custom_column_1_custom_column_22_without_nulls_idx
                        on custom_table (custom_column_1, custom_column_22) where custom_column_22 is not null;"""),
                normalizeEndings("""
                    /* table_with_very_very_very_long_name_column_with_very_very_very_long_name_without_nulls_idx */
                    create index concurrently if not exists table_with_very_very_very_long_name_3202677_without_nulls_idx
                        on schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name) \
                    where column_with_very_very_very_long_name is not null;"""));
    }
}
