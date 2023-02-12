/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.validation;

import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class Validators {

    private Validators() {
        throw new UnsupportedOperationException();
    }

    public static long valueIsPositive(final long argumentValue, @Nonnull final String argumentName) {
        if (argumentValue <= 0) {
            throw new IllegalArgumentException(argumentName + " should be greater than zero");
        }
        return argumentValue;
    }

    @Nonnull
    public static String tableNameNotBlank(@Nonnull final String tableName) {
        return notBlank(tableName, "tableName");
    }

    @Nonnull
    public static String indexNameNotBlank(@Nonnull final String indexName) {
        return notBlank(indexName, "indexName");
    }

    @Nonnull
    public static String notBlank(@Nonnull final String argumentValue, @Nonnull final String argumentName) {
        if (Objects.requireNonNull(argumentValue, argumentName + " cannot be null").isBlank()) {
            throw new IllegalArgumentException(argumentName + " cannot be blank");
        }
        return argumentValue;
    }

    public static long sizeNotNegative(final long sizeInBytes, @Nonnull final String argumentName) {
        return argumentNotNegative(sizeInBytes, argumentName);
    }

    public static long countNotNegative(final long count, @Nonnull final String argumentName) {
        return argumentNotNegative(count, argumentName);
    }

    public static int argumentNotNegative(final int argumentValue, @Nonnull final String argumentName) {
        if (argumentValue < 0) {
            throw new IllegalArgumentException(argumentName + " cannot be less than zero");
        }
        return argumentValue;
    }

    private static long argumentNotNegative(final long argumentValue, @Nonnull final String argumentName) {
        if (argumentValue < 0L) {
            throw new IllegalArgumentException(argumentName + " cannot be less than zero");
        }
        return argumentValue;
    }

    public static int validPercent(final int percentValue, @Nonnull final String argumentName) {
        if (percentValue < 0 || percentValue > 100) {
            throw new IllegalArgumentException(argumentName + " should be in the range from 0 to 100 inclusive");
        }
        return percentValue;
    }

    public static void validateThatTableIsTheSame(@Nonnull final String expectedTableName, @Nonnull final List<? extends TableNameAware> rows) {
        final boolean tableIsTheSame = rows.stream().allMatch(i -> i.getTableName().equals(expectedTableName));
        if (!tableIsTheSame) {
            throw new IllegalArgumentException("Table name is not the same within given rows");
        }
    }

    public static <T> void validateThatNotEmpty(@Nonnull final List<T> columnsInConstraint) {
        if (columnsInConstraint.isEmpty()) {
            throw new IllegalArgumentException("columnsInConstraint cannot be empty");
        }
    }
}
