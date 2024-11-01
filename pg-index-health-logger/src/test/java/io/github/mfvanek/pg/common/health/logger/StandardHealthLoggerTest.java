/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.common.maintenance.DatabaseChecks;

import javax.annotation.Nonnull;

class StandardHealthLoggerTest extends HealthLoggerTestBase {

    @Nonnull
    @Override
    protected HealthLogger getHealthLogger() {
        return new StandardHealthLogger(getConnectionCredentials(), getConnectionFactory(), DatabaseChecks::new);
    }

    @Nonnull
    @Override
    protected String[] getExpectedValue() {
        return new String[]{
            "invalid_indexes:1",
            "duplicated_indexes:2",
            "foreign_keys_without_index:7",
            "tables_without_primary_key:2",
            "indexes_with_null_values:1",
            "indexes_with_bloat:17",
            "tables_with_bloat:2",
            "intersected_indexes:11",
            "unused_indexes:12",
            "tables_with_missing_indexes:0",
            "tables_without_description:6",
            "columns_without_description:27",
            "columns_with_json_type:1",
            "columns_with_serial_types:3",
            "functions_without_description:2",
            "indexes_with_boolean:1",
            "not_valid_constraints:2",
            "btree_indexes_on_array_columns:2",
            "sequence_overflow:3",
            "primary_keys_with_serial_types:1",
            "duplicated_foreign_keys:3",
            "intersected_foreign_keys:1",
            "possible_object_name_overflow:2",
            "tables_not_linked_to_others:2",
            "foreign_keys_with_unmatched_column_type:2"
        };
    }

    @Nonnull
    @Override
    protected String getExpectedValueForDefaultSchema(@Nonnull final LoggingKey key) {
        return key.getSubKeyName() + ":0";
    }
}
