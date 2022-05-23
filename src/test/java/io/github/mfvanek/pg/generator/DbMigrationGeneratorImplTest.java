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

import java.util.Arrays;
import java.util.Collections;

import static io.github.mfvanek.pg.generator.PgIdentifierNameGeneratorTest.nullableColumnWithSchema;
import static io.github.mfvanek.pg.generator.PgIndexOnForeignKeyGeneratorTest.severalColumnsWithNulls;
import static org.assertj.core.api.Assertions.assertThat;

class DbMigrationGeneratorImplTest {

    @Test
    void generateForSingleForeignKey() {
        final DbMigrationGenerator generator = new DbMigrationGeneratorImpl();
        final String result = generator.generate(Collections.singletonList(nullableColumnWithSchema()), GeneratingOptions.builder().build());
        assertThat(result)
                .isNotBlank()
                .isEqualTo("/* table_with_very_very_very_long_name_column_with_very_very_very_long_name_without_nulls_idx */\n" +
                        "create index concurrently if not exists table_with_very_very_very_long_name_3202677_without_nulls_idx\n" +
                        "    on schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name) " +
                        "where column_with_very_very_very_long_name is not null;");
    }

    @Test
    void generateForSeveralForeignKeys() {
        final DbMigrationGenerator generator = new DbMigrationGeneratorImpl();
        final String result = generator.generate(
                Arrays.asList(severalColumnsWithNulls(), severalColumnsWithNulls(), nullableColumnWithSchema()),
                GeneratingOptions.builder().build());
        assertThat(result)
                .isNotBlank()
                .isEqualTo("create index concurrently if not exists table_column_1_column_2_without_nulls_idx\n" +
                        "    on table (column_1, column_2) where column_2 is not null;\n" +
                        "\n" +
                        "create index concurrently if not exists table_column_1_column_2_without_nulls_idx\n" +
                        "    on table (column_1, column_2) where column_2 is not null;\n" +
                        "\n" +
                        "/* table_with_very_very_very_long_name_column_with_very_very_very_long_name_without_nulls_idx */\n" +
                        "create index concurrently if not exists table_with_very_very_very_long_name_3202677_without_nulls_idx\n" +
                        "    on schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name) " +
                        "where column_with_very_very_very_long_name is not null;");
    }
}
