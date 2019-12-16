/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import javax.annotation.Nonnull;
import java.util.Objects;

public enum SimpleLoggingKey implements LoggingKey {

    INVALID_INDEXES("invalid_indexes"),
    DUPLICATED_INDEXES("duplicated_indexes"),
    INTERSECTED_INDEXES("intersected_indexes"),
    UNUSED_INDEXES("unused_indexes"),
    FOREIGN_KEYS("foreign_keys_without_index"),
    TABLES_WITH_MISSING_INDEXES("tables_with_missing_indexes"),
    TABLES_WITHOUT_PK("tables_without_primary_key"),
    INDEXES_WITH_NULLS("indexes_with_null_values");

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
}

