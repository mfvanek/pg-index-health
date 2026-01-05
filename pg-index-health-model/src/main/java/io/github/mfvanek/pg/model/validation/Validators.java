/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.validation;

import io.github.mfvanek.pg.model.column.ColumnNameAware;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.index.IndexNameAware;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Utility class providing various validation methods.
 * This class cannot be instantiated.
 */
public final class Validators {

    private Validators() {
        throw new UnsupportedOperationException();
    }

    /**
     * Ensures the given value is positive.
     *
     * @param argumentValue the value to check
     * @param argumentName  the name of the argument being checked
     * @return the provided value if it is positive
     * @throws IllegalArgumentException if the value is not positive
     */
    public static long valueIsPositive(final long argumentValue, final String argumentName) {
        if (argumentValue <= 0) {
            throw new IllegalArgumentException(argumentName + " should be greater than zero");
        }
        return argumentValue;
    }

    /**
     * Ensures the provided table name is not blank.
     *
     * @param tableName the table name to check
     * @return the provided table name if it is not blank
     * @throws IllegalArgumentException if the table name is blank
     */
    public static String tableNameNotBlank(final String tableName) {
        return notBlank(tableName, TableNameAware.TABLE_NAME_FIELD);
    }

    /**
     * Ensures the provided index name is not blank.
     *
     * @param indexName the index name to check
     * @return the provided index name if it is not blank
     * @throws IllegalArgumentException if the index name is blank
     */
    public static String indexNameNotBlank(final String indexName) {
        return notBlank(indexName, IndexNameAware.INDEX_NAME_FIELD);
    }

    /**
     * Ensures the provided string argument is not blank.
     *
     * @param argumentValue the string argument to check
     * @param argumentName  the name of the argument being checked
     * @return the provided string argument if it is not blank
     * @throws IllegalArgumentException if the argument is blank
     */
    public static String notBlank(final String argumentValue, final String argumentName) {
        if (Objects.requireNonNull(argumentValue, argumentName + " cannot be null").isBlank()) {
            throw new IllegalArgumentException(argumentName + " cannot be blank");
        }
        return argumentValue;
    }

    /**
     * Ensures the given size is not negative.
     *
     * @param sizeInBytes  the size to check
     * @param argumentName the name of the argument being checked
     * @return the provided size if it is not negative
     * @throws IllegalArgumentException if the size is negative
     */
    public static long sizeNotNegative(final long sizeInBytes, final String argumentName) {
        return argumentNotNegative(sizeInBytes, argumentName);
    }

    /**
     * Ensures the given count is not negative.
     *
     * @param count        the count to check
     * @param argumentName the name of the argument being checked
     * @return the provided count if it is not negative
     * @throws IllegalArgumentException if the count is negative
     */
    public static long countNotNegative(final long count, final String argumentName) {
        return argumentNotNegative(count, argumentName);
    }

    /**
     * Ensures the given integer value is not negative.
     *
     * @param argumentValue the value to check
     * @param argumentName  the name of the argument being checked
     * @return the provided value if it is not negative
     * @throws IllegalArgumentException if the value is negative
     */
    public static int argumentNotNegative(final int argumentValue, final String argumentName) {
        if (argumentValue < 0) {
            throw new IllegalArgumentException(argumentName + " cannot be less than zero");
        }
        return argumentValue;
    }

    private static long argumentNotNegative(final long argumentValue, final String argumentName) {
        if (argumentValue < 0L) {
            throw new IllegalArgumentException(argumentName + " cannot be less than zero");
        }
        return argumentValue;
    }

    /**
     * Validates that all rows have the same table name as the expected table name.
     *
     * @param expectedTableName the expected table name
     * @param rows              the rows to check
     * @throws IllegalArgumentException if any row has a different table name
     */
    public static void validateThatTableIsTheSame(final String expectedTableName, final Collection<? extends TableNameAware> rows) {
        final boolean tableIsTheSame = rows.stream().allMatch(i -> i.getTableName().equals(expectedTableName));
        if (!tableIsTheSame) {
            throw new IllegalArgumentException("Table name is not the same within given rows");
        }
    }

    /**
     * Validates that all rows have the same table name.
     *
     * @param rows the rows to check
     * @param <T>  the type of the list elements
     */
    public static <T extends TableNameAware & DbObject> void validateThatTableIsTheSame(final List<T> rows) {
        final String tableName = validateThatContainsAtLeastTwoRows(rows).get(0).getTableName();
        validateThatTableIsTheSame(tableName, rows);
    }

    private static <T extends TableNameAware & DbObject> List<T> validateThatContainsAtLeastTwoRows(final List<T> rows) {
        final int size = Objects.requireNonNull(rows, "rows cannot be null").size();
        if (0 == size) {
            throw new IllegalArgumentException("rows cannot be empty");
        }
        if (size < 2) {
            throw new IllegalArgumentException("rows should contains at least two items");
        }
        return rows;
    }

    /**
     * Ensures the provided list is not empty.
     *
     * @param columns the list to check
     * @param <T>     the type of the list elements
     * @throws IllegalArgumentException if the list is empty
     */
    public static <T extends ColumnNameAware> void validateThatNotEmpty(final Collection<T> columns) {
        if (columns.isEmpty()) {
            throw new IllegalArgumentException(ColumnsAware.COLUMNS_FIELD + " cannot be empty");
        }
    }

    /**
     * Ensures the given percent value is between 0.0 and 100.0 inclusive.
     *
     * @param percentValue the percent value to check
     * @param argumentName the name of the argument being checked
     * @return the provided percent value if it is valid
     * @throws IllegalArgumentException if the percent value is outside the valid range
     */
    public static double validPercent(final double percentValue, final String argumentName) {
        if (percentValue < 0.0 || percentValue > 100.0) {
            throw new IllegalArgumentException(argumentName + " should be in the range from 0.0 to 100.0 inclusive");
        }
        return percentValue;
    }
}
