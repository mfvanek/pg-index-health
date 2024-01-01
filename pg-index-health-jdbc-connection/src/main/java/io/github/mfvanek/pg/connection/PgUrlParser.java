/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public final class PgUrlParser {

    public static final String URL_HEADER = "jdbc:postgresql://";
    private static final String PG_URL = "pgUrl";
    private static final Map<String, String> DEFAULT_URL_PARAMETERS = Map.ofEntries(
            Map.entry("targetServerType", "primary"),
            Map.entry("hostRecheckSeconds", "2"),
            Map.entry("connectTimeout", "1"),
            Map.entry("socketTimeout", "600")
    );

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
    static List<Map.Entry<String, Integer>> extractHostNames(@Nonnull final String pgUrl) {
        PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, PG_URL);
        final String allHostsWithPort = extractAllHostsWithPort(pgUrl);
        return Arrays.stream(allHostsWithPort.split(","))
                .filter(Predicate.not(String::isBlank))
                .map(h -> {
                    final String[] hostToPort = h.split(":");
                    return Map.entry(hostToPort[0], Integer.parseInt(hostToPort[1]));
                })
                .distinct()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toUnmodifiableList());
    }

    @Nonnull
    static String extractDatabaseName(@Nonnull final Set<String> pgUrls) {
        final String pgUrl = pgUrls.iterator().next();
        final int lastIndexOfSlash = pgUrl.lastIndexOf('/');
        final String dbNameWithParams = pgUrl.substring(lastIndexOfSlash);
        final int lastIndex = dbNameWithParams.lastIndexOf('?');
        if (lastIndex >= 0) {
            return dbNameWithParams.substring(0, lastIndex);
        }
        return dbNameWithParams;
    }

    @Nonnull
    private static String extractAllHostsWithPort(@Nonnull final String pgUrl) {
        final int lastIndex = pgUrl.lastIndexOf('/');
        if (lastIndex >= URL_HEADER.length()) {
            return pgUrl.substring(URL_HEADER.length(), lastIndex);
        }
        return pgUrl.substring(URL_HEADER.length());
    }

    @Nonnull
    public static String buildCommonUrlToPrimary(@Nonnull final String firstPgUrl,
                                                 @Nonnull final String secondPgUrl) {
        return buildCommonUrlToPrimary(Set.of(firstPgUrl, secondPgUrl));
    }

    @Nonnull
    public static String buildCommonUrlToPrimary(@Nonnull final String firstPgUrl,
                                                 @Nonnull final String secondPgUrl,
                                                 @Nonnull final Map<String, String> urlParameters) {
        return buildCommonUrlToPrimary(Set.of(firstPgUrl, secondPgUrl), urlParameters);
    }

    @Nonnull
    public static String buildCommonUrlToPrimary(@Nonnull final Set<String> pgUrls) {
        return buildCommonUrlToPrimary(pgUrls, Map.of());
    }

    @Nonnull
    public static String buildCommonUrlToPrimary(@Nonnull final Set<String> pgUrls,
                                                 @Nonnull final Map<String, String> urlParameters) {
        final String additionalUrlParams = constructUrlParameters(urlParameters);
        return URL_HEADER + pgUrls.stream()
                .map(PgUrlParser::extractAllHostsWithPort)
                .sorted()
                .collect(Collectors.joining(",")) +
                extractDatabaseName(pgUrls) + additionalUrlParams;
    }

    @Nonnull
    static String constructUrlParameters(@Nonnull final Map<String, String> urlParameters) {
        final Map<String, String> jointUrlParameters = new TreeMap<>(urlParameters);
        DEFAULT_URL_PARAMETERS.forEach(jointUrlParameters::putIfAbsent);

        final String additionalParameters = jointUrlParameters.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        return "?" + additionalParameters;
    }
}
