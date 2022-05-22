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
    void generateForSingleNullableColumn() {
        final PgIndexOnForeignKeyGenerator generator = PgIndexOnForeignKeyGenerator.of(nullableColumnWithSchema(), GeneratingOptions.builder().build());
        assertThat(generator.generate())
                .isEqualTo("/* table_with_very_very_very_long_name_column_with_very_very_very_long_name_without_nulls_idx */\n" +
                        "create index concurrently if not exists table_with_very_very_very_long_name_3202677_without_nulls_idx\n" +
                        "    on schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name) " +
                        "where column_with_very_very_very_long_name is not null;");
    }
}
