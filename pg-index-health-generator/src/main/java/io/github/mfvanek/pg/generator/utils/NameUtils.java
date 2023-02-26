/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator.utils;

import io.github.mfvanek.pg.model.table.TableNameAware;

import javax.annotation.Nonnull;

public final class NameUtils {

    private NameUtils() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public static String getTableNameWithoutSchema(@Nonnull final TableNameAware tableNameAware) {
        final String tableName = tableNameAware.getTableName();
        final int index = tableName.indexOf('.');
        final boolean containsSchema = index >= 0;
        if (containsSchema) {
            return tableName.substring(index + 1);
        }
        return tableName;
    }
}
