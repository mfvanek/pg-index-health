/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

final class PgUrlParser {

    static final String URL_HEADER = "jdbc:postgresql://";
    private static final String PG_URL = "pgUrl";

    private PgUrlParser() {
        throw new UnsupportedOperationException();
    }

    static boolean isReplicaUrl(@Nonnull final String pgUrl) {
        PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, PG_URL);
        return pgUrl.contains("targetServerType=slave") ||
                pgUrl.contains("targetServerType=secondary");
    }

    // For example, jdbc:postgresql://host-1:6432/db_name?param=value
    @Nonnull
    static List<Map.Entry<String, String>> extractNameWithPortAndUrlForEachHost(@Nonnull final String pgUrl) {
        PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, PG_URL);
        final int lastIndex = pgUrl.lastIndexOf('/');
        final String dbNameWithParams = pgUrl.substring(lastIndex);
        final String dbNameWithParamsForReplica = convertToReplicaConnectionString(dbNameWithParams);
        final String allHostsWithPort = extractAllHostsWithPort(pgUrl);
        return Arrays.stream(allHostsWithPort.split(","))
                .distinct()
                .sorted()
                .map(h -> Map.entry(h, URL_HEADER + h + dbNameWithParamsForReplica))
                .collect(Collectors.toUnmodifiableList());
    }

    @Nonnull
    private static String convertToReplicaConnectionString(@Nonnull final String dbNameWithParams) {
        final List<String> primaryServerTypes = List.of("targetServerType=primary", "targetServerType=master");
        for (final String serverType : primaryServerTypes) {
            if (dbNameWithParams.contains(serverType)) {
                return dbNameWithParams.replace(serverType, "targetServerType=any");
            }
        }
        return dbNameWithParams;
    }

    @Nonnull
    static SortedSet<String> extractHostNames(@Nonnull final String pgUrl) {
        PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, PG_URL);
        final String allHostsWithPort = extractAllHostsWithPort(pgUrl);
        return Collections.unmodifiableSortedSet(Arrays.stream(allHostsWithPort.split(","))
                .filter(Predicate.not(String::isBlank))
                .map(h -> h.substring(0, h.lastIndexOf(':')))
                .sorted()
                .collect(Collectors.toCollection(TreeSet::new)));
    }

    @Nonnull
    private static String extractAllHostsWithPort(@Nonnull final String pgUrl) {
        final int lastIndex = pgUrl.lastIndexOf('/');
        if (lastIndex >= URL_HEADER.length()) {
            return pgUrl.substring(URL_HEADER.length(), lastIndex);
        }
        return pgUrl.substring(URL_HEADER.length());
    }
}
