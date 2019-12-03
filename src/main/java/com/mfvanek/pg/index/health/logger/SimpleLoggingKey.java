/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import javax.annotation.Nonnull;
import java.util.Objects;

public enum SimpleLoggingKey implements LoggingKey {

    INVALID_INDICES("invalid_indices"),
    DUPLICATED_INDICES("duplicated_indices"),
    INTERSECTED_INDICES("intersected_indices"),
    UNUSED_INDICES("unused_indices"),
    FOREIGN_KEYS("foreign_keys_without_index"),
    TABLES_WITH_MISSING_INDICES("tables_with_missing_indices"),
    TABLES_WITHOUT_PK("tables_without_primary_key"),
    INDICES_WITH_NULLS("indices_with_null_values");

    final String subKeyName;

    SimpleLoggingKey(@Nonnull final String subKeyName) {
        this.subKeyName = Objects.requireNonNull(subKeyName);
    }

    @Nonnull
    @Override
    public String getKeyName() {
        return "db_indices_health";
    }

    @Nonnull
    @Override
    public String getSubKeyName() {
        return subKeyName;
    }
}

