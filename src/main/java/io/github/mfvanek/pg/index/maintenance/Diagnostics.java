/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.index.maintenance;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A list of all the supported diagnostics with corresponding sql queries.
 *
 * @author Ivan Vakhrushev
 */
public enum Diagnostics {

    BLOATED_INDEXES("bloated_indexes.sql"),
    BLOATED_TABLES("bloated_tables.sql"),
    DUPLICATED_INDEXES("duplicated_indexes.sql"),
    FOREIGN_KEYS_WITHOUT_INDEX("foreign_keys_without_index.sql"),
    INDEXES_WITH_NULL_VALUES("indexes_with_null_values.sql"),
    INTERSECTED_INDEXES("intersected_indexes.sql"),
    INVALID_INDEXES("invalid_indexes.sql"),
    TABLES_WITH_MISSING_INDEXES("tables_with_missing_indexes.sql"),
    TABLES_WITHOUT_PRIMARY_KEY("tables_without_primary_key.sql"),
    UNUSED_INDEXES("unused_indexes.sql");

    private final String sqlQueryFileName;

    Diagnostics(@Nonnull final String sqlQueryFileName) {
        this.sqlQueryFileName = Objects.requireNonNull(sqlQueryFileName, "sqlQueryFileName");
    }

    @Nonnull
    public String getSqlQueryFileName() {
        return sqlQueryFileName;
    }
}
