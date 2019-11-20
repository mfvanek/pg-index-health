/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

final class Validators {

    private Validators() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    static String tableNameNotBlank(@Nonnull final String tableName) {
        return notBlank(tableName, "tableName");
    }

    @Nonnull
    static String indexNameNotBlank(@Nonnull final String indexName) {
        return notBlank(indexName, "indexName");
    }

    @Nonnull
    static String notBlank(@Nonnull final String argumentValue, @Nonnull final String argumentName) {
        if (StringUtils.isBlank(Objects.requireNonNull(argumentValue, argumentName + " cannot be null"))) {
            throw new IllegalArgumentException(argumentName);
        }
        return argumentValue;
    }

    static long sizeNotNegative(final long sizeInBytes, @Nonnull final String argumentName) {
        return argumentNotNegative(sizeInBytes, argumentName);
    }

    static long countNotNegative(final long count, @Nonnull final String argumentName) {
        return argumentNotNegative(count, argumentName);
    }

    private static long argumentNotNegative(final long argumentValue, @Nonnull final String argumentName) {
        if (argumentValue < 0L) {
            throw new IllegalArgumentException(argumentName + " cannot be less than zero");
        }
        return argumentValue;
    }

    @Nonnull
    static List<IndexWithSize> validateThatTableIsTheSame(@Nonnull final List<IndexWithSize> duplicatedIndices) {
        final String tableName = validateThatContainsAtLeastTwoRows(duplicatedIndices).get(0).getTableName();
        final boolean tableIsTheSame = duplicatedIndices.stream().allMatch(i -> i.getTableName().equals(tableName));
        if (!tableIsTheSame) {
            throw new IllegalArgumentException("Table name is not the same within given rows");
        }
        return duplicatedIndices;
    }

    @Nonnull
    private static List<IndexWithSize> validateThatContainsAtLeastTwoRows(@Nonnull final List<IndexWithSize> duplicatedIndices) {
        final int size = Objects.requireNonNull(duplicatedIndices).size();
        if (0 == size) {
            throw new IllegalArgumentException("duplicatedIndices cannot be empty");
        }
        if (size < 2) {
            throw new IllegalArgumentException("duplicatedIndices should contains at least two rows");
        }
        return duplicatedIndices;
    }

    @Nonnull
    static List<String> validateThatNotEmpty(@Nonnull final List<String> columnsInConstraint) {
        if (CollectionUtils.isEmpty(columnsInConstraint)) {
            throw new IllegalArgumentException("columnsInConstraint cannot be empty");
        }
        return columnsInConstraint;
    }
}
