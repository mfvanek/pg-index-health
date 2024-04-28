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

import java.util.Objects;
import javax.annotation.Nonnull;

public enum SimpleLoggingKey implements LoggingKey {

    INVALID_INDEXES("invalid_indexes"),
    DUPLICATED_INDEXES("duplicated_indexes"),
    INTERSECTED_INDEXES("intersected_indexes"),
    UNUSED_INDEXES("unused_indexes"),
    FOREIGN_KEYS_WITHOUT_INDEX("foreign_keys_without_index"),
    TABLES_WITH_MISSING_INDEXES("tables_with_missing_indexes"),
    TABLES_WITHOUT_PRIMARY_KEY("tables_without_primary_key"),
    INDEXES_WITH_NULL_VALUES("indexes_with_null_values"),
    BLOATED_INDEXES("indexes_with_bloat"),
    BLOATED_TABLES("tables_with_bloat"),
    TABLES_WITHOUT_DESCRIPTION("tables_without_description"),
    COLUMNS_WITHOUT_DESCRIPTION("columns_without_description"),
    COLUMNS_WITH_JSON_TYPE("columns_with_json_type"),
    COLUMNS_WITH_SERIAL_TYPES("columns_with_serial_types"),
    FUNCTIONS_WITHOUT_DESCRIPTION("functions_without_description"),
    INDEXES_WITH_BOOLEAN("indexes_with_boolean");

    private final String subKeyName;

    SimpleLoggingKey(@Nonnull final String subKeyName) {
        this.subKeyName = Objects.requireNonNull(subKeyName);
    }

    @Nonnull
    @Override
    public String getKeyName() {
        return "db_indexes_health";
    }

    @Nonnull
    @Override
    public String getSubKeyName() {
        return subKeyName;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return subKeyName.replace('_', ' ');
    }
}
