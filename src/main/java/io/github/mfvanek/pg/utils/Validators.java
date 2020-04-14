/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.model.IndexWithSize;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

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
        if (StringUtils.isBlank(Objects.requireNonNull(argumentValue, argumentName + " cannot be null"))) {
            throw new IllegalArgumentException(argumentName);
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

    public static int validPercent(final int percentValue, @Nonnull final String argumentName) {
        if (percentValue < 0 || percentValue > 100) {
            throw new IllegalArgumentException(argumentName + " should be in the range from 0 to 100 inclusive");
        }
        return percentValue;
    }

    private static long argumentNotNegative(final long argumentValue, @Nonnull final String argumentName) {
        if (argumentValue < 0L) {
            throw new IllegalArgumentException(argumentName + " cannot be less than zero");
        }
        return argumentValue;
    }

    @Nonnull
    public static List<IndexWithSize> validateThatTableIsTheSame(@Nonnull final List<IndexWithSize> duplicatedIndexes) {
        final String tableName = validateThatContainsAtLeastTwoRows(duplicatedIndexes).get(0).getTableName();
        final boolean tableIsTheSame = duplicatedIndexes.stream().allMatch(i -> i.getTableName().equals(tableName));
        if (!tableIsTheSame) {
            throw new IllegalArgumentException("Table name is not the same within given rows");
        }
        return duplicatedIndexes;
    }

    @Nonnull
    private static List<IndexWithSize> validateThatContainsAtLeastTwoRows(
            @Nonnull final List<IndexWithSize> duplicatedIndexes) {
        final int size = Objects.requireNonNull(duplicatedIndexes).size();
        if (0 == size) {
            throw new IllegalArgumentException("duplicatedIndexes cannot be empty");
        }
        if (size < 2) {
            throw new IllegalArgumentException("duplicatedIndexes should contains at least two rows");
        }
        return duplicatedIndexes;
    }

    @Nonnull
    public static List<String> validateThatNotEmpty(@Nonnull final List<String> columnsInConstraint) {
        if (CollectionUtils.isEmpty(columnsInConstraint)) {
            throw new IllegalArgumentException("columnsInConstraint cannot be empty");
        }
        return columnsInConstraint;
    }

    public static String paramValueNotNull(@Nonnull final String value, @Nonnull final String message) {
        return Objects.requireNonNull(value, message).trim();
    }

    @Nonnull
    public static String validateSqlFileName(@Nonnull final String sqlFileName) {
        final String fileName = notBlank(sqlFileName, "sqlFileName").toLowerCase();
        if (!fileName.endsWith(".sql")) {
            throw new IllegalArgumentException("only *.sql files are supported");
        }
        return fileName;
    }
}
