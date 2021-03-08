/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

final class PgUrlParser {

    static final String URL_HEADER = "jdbc:postgresql://";

    private PgUrlParser() {
        throw new UnsupportedOperationException();
    }

    static boolean isReplicaUrl(@Nonnull final String pgUrl) {
        PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, "pgUrl");
        return pgUrl.contains("targetServerType=slave") ||
                pgUrl.contains("targetServerType=secondary");
    }

    // For example, jdbc:postgresql://host-1:6432/db_name?param=value
    @Nonnull
    static List<Pair<String, String>> extractNameWithPortAndUrlForEachHost(@Nonnull final String pgUrl) {
        PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, "pgUrl");
        final int lastIndex = pgUrl.lastIndexOf('/');
        final String dbNameWithParams = pgUrl.substring(lastIndex);
        final String dbNameWithParamsForReplica = convertToReplicaConnectionString(dbNameWithParams);
        final String allHostsWithPort = extractAllHostsWithPort(pgUrl);
        return Arrays.stream(allHostsWithPort.split(","))
                .distinct()
                .sorted()
                .map(h -> Pair.of(h, URL_HEADER + h + dbNameWithParamsForReplica))
                .collect(Collectors.toList());
    }

    @Nonnull
    private static String convertToReplicaConnectionString(@Nonnull final String dbNameWithParams) {
        final List<String> primaryServerTypes = Arrays.asList("targetServerType=primary", "targetServerType=master");
        for (final String serverType : primaryServerTypes) {
            if (dbNameWithParams.contains(serverType)) {
                return dbNameWithParams.replace(serverType, "targetServerType=any");
            }
        }
        return dbNameWithParams;
    }

    @Nonnull
    static Set<String> extractHostNames(@Nonnull final String pgUrl) {
        PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, "pgUrl");
        final String allHostsWithPort = extractAllHostsWithPort(pgUrl);
        return Arrays.stream(allHostsWithPort.split(","))
                .filter(StringUtils::isNotBlank)
                .map(h -> h.substring(0, h.lastIndexOf(':')))
                .collect(Collectors.toSet());
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
