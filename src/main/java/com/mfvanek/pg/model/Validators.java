/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Objects;

final class Validators {

    private Validators() {
        throw new UnsupportedOperationException();
    }

    static String tableNameNotBlank(@Nonnull final String tableName) {
        if (StringUtils.isBlank(Objects.requireNonNull(tableName, "tableName cannot be null"))) {
            throw new IllegalArgumentException("tableName");
        }
        return tableName;
    }

    static String indexNameNotBlank(@Nonnull final String indexName) {
        if (StringUtils.isBlank(Objects.requireNonNull(indexName, "indexName cannot be null"))) {
            throw new IllegalArgumentException("indexName");
        }
        return indexName;
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
}
