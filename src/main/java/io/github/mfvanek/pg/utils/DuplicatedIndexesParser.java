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

import javax.annotation.Nonnull;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class DuplicatedIndexesParser {

    private DuplicatedIndexesParser() {
        throw new UnsupportedOperationException();
    }

    public static List<Map.Entry<String, Long>> parseAsIndexNameAndSize(@Nonnull final String duplicatedAsString) {
        Validators.notBlank(duplicatedAsString, "duplicatedAsString");
        final String[] indexes = duplicatedAsString.split("; ");
        return Arrays.stream(indexes)
                .map(s -> s.split(","))
                .filter(a -> a[0].trim().startsWith("idx=") && a[1].trim().startsWith("size="))
                .map(a -> {
                    final String indexName = a[0].trim().substring("idx=".length());
                    final String sizeAsString = a[1].trim().substring("size=".length());
                    return new AbstractMap.SimpleEntry<>(indexName, Long.parseLong(sizeAsString));
                })
                .collect(Collectors.toList());
    }
}
