/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

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
    INDEXES_WITH_NULLS("indexes_with_null_values"),
    INDEXES_BLOAT("indexes_bloat"),
    TABLES_BLOAT("tables_bloat");

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

