/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator.utils;

import io.github.mfvanek.pg.model.table.TableNameAware;

public final class NameUtils {

    private NameUtils() {
        throw new UnsupportedOperationException();
    }

    public static String getTableNameWithoutSchema(final TableNameAware tableNameAware) {
        final String tableName = tableNameAware.getTableName();
        final int index = tableName.indexOf('.');
        final boolean containsSchema = index >= 0;
        if (containsSchema) {
            return tableName.substring(index + 1);
        }
        return tableName;
    }
}
