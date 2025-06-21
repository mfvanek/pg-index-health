/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index.utils;

import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Utility class for parsing duplicated index information from a formatted string and combining database objects into lists.
 * This class is not instantiable.
 */
public final class DuplicatedIndexesParser {

    private DuplicatedIndexesParser() {
        throw new UnsupportedOperationException();
    }

    /**
     * Parses a formatted string containing duplicated index information and returns a list of entries with index names
     * and their corresponding sizes.
     * <p>
     * Each entry in the list represents a duplicated index, with the index name as the key and the index size as the value.
     * The input string should follow a format where each duplicated index entry is separated by a semicolon and space ("; "),
     * with individual attributes of each entry (e.g., "idx=" and "size=") separated by commas.
     * </p>
     *
     * @param duplicatedAsString a non-blank string containing duplicated index details; must be non-null
     * @return an unmodifiable list of index name and size pairs
     * @throws NullPointerException     if {@code duplicatedAsString} is null
     * @throws IllegalArgumentException if {@code duplicatedAsString} is blank
     */
    public static List<Map.Entry<String, Long>> parseAsIndexNameAndSize(final String duplicatedAsString) {
        Validators.notBlank(duplicatedAsString, "duplicatedAsString");
        final String[] indexes = duplicatedAsString.split("; ");
        return Arrays.stream(indexes)
            .map(s -> s.split(","))
            .filter(a -> a[0].trim().startsWith("idx=") && a[1].trim().startsWith("size="))
            .map(a -> {
                final String indexName = a[0].trim().substring("idx=".length());
                final String sizeAsString = a[1].trim().substring("size=".length());
                return Map.entry(indexName, Long.valueOf(sizeAsString));
            })
            .toList();
    }

    /**
     * Combines given database objects into list.
     *
     * @param firstObject  first database object; should be non-null.
     * @param secondObject second database object; should be non-null.
     * @param otherObjects other database objects.
     * @param <T>          the type of the list elements
     * @return combined list of database objects
     */
    @SafeVarargs
    public static <T extends TableNameAware & DbObject> List<T> combine(final T firstObject,
                                                                        final T secondObject,
                                                                        final T... otherObjects) {
        Objects.requireNonNull(firstObject, "firstObject cannot be null");
        Objects.requireNonNull(secondObject, "secondObject cannot be null");
        if (Stream.of(otherObjects).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("otherObjects cannot contain nulls");
        }
        final Stream<T> basePart = Stream.of(firstObject, secondObject);
        return Stream.concat(basePart, Stream.of(otherObjects))
            .toList();
    }
}
