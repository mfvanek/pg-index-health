/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.host;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static io.github.mfvanek.pg.connection.host.PgUrlValidators.pgUrlNotBlankAndValid;

/**
 * Utility class for parsing and manipulating PostgreSQL connection URLs.
 */
public final class PgUrlParser {

    /**
     * Header prefix for PostgreSQL JDBC URLs.
     */
    public static final String URL_HEADER = "jdbc:postgresql://";

    /**
     * The URL prefix used in Testcontainers to initialize PostgreSQL containers.
     * <p>
     * Testcontainers provides a special JDBC URL format that allows for on-the-fly creation and management
     * of PostgreSQL database containers during tests. This prefix is part of the JDBC URL and signals
     * Testcontainers to handle the lifecycle of the container automatically.
     * </p>
     *
     * @see <a href="https://java.testcontainers.org/modules/databases/jdbc/">Testcontainers JDBC Support</a>
     * @since 0.14.2
     */
    public static final String TESTCONTAINERS_PG_URL_PREFIX = "jdbc:tc:postgresql:";

    private static final Map<String, String> DEFAULT_URL_PARAMETERS = Map.ofEntries(
        Map.entry("targetServerType", "primary"),
        Map.entry("hostRecheckSeconds", "2"),
        Map.entry("connectTimeout", "1"),
        Map.entry("socketTimeout", "600")
    );

    private PgUrlParser() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the given PostgreSQL URL points to a replica.
     *
     * @param pgUrl the PostgreSQL connection URL; must be valid and non-blank.
     * @return {@code true} if the URL specifies a replica as the target server type.
     */
    static boolean isReplicaUrl(final String pgUrl) {
        pgUrlNotBlankAndValid(pgUrl);
        return pgUrl.contains("targetServerType=slave") ||
            pgUrl.contains("targetServerType=secondary");
    }

    /**
     * Extracts host-port pairs and builds replica-compatible connection URLs for each host.
     *
     * @param pgUrl the PostgreSQL connection URL; must be valid and non-blank.
     * @return a list of host-port pairs and corresponding replica-compatible URLs.
     */
    public static List<Map.Entry<String, String>> extractNameWithPortAndUrlForEachHost(final String pgUrl) {
        // For example, jdbc:postgresql://host-1:6432/db_name?param=value
        pgUrlNotBlankAndValid(pgUrl);
        final int lastIndex = pgUrl.lastIndexOf('/');
        final String dbNameWithParams = pgUrl.substring(lastIndex);
        final String dbNameWithParamsForReplica = convertToReplicaConnectionString(dbNameWithParams);
        final String allHostsWithPort = extractAllHostsWithPort(pgUrl);
        return Arrays.stream(allHostsWithPort.split(","))
            .distinct()
            .sorted()
            .map(h -> Map.entry(h, URL_HEADER + h + dbNameWithParamsForReplica))
            .toList();
    }

    /**
     * Converts a given part of connection string to a replica-compatible connection string.
     *
     * @param dbNameWithParams the database name and parameters from the URL.
     * @return the replica-compatible connection string.
     */
    private static String convertToReplicaConnectionString(final String dbNameWithParams) {
        final List<String> primaryServerTypes = List.of("targetServerType=primary", "targetServerType=master");
        for (final String serverType : primaryServerTypes) {
            if (dbNameWithParams.contains(serverType)) {
                return dbNameWithParams.replace(serverType, "targetServerType=any");
            }
        }
        return dbNameWithParams;
    }

    /**
     * Extracts host names and ports from a PostgreSQL connection URL.
     *
     * @param pgUrl the PostgreSQL connection URL; must be valid and non-blank.
     * @return a list of host-port pairs.
     */
    static List<Map.Entry<String, Integer>> extractHostNames(final String pgUrl) {
        final String allHostsWithPort = extractAllHostsWithPort(pgUrlNotBlankAndValid(pgUrl));
        return Arrays.stream(allHostsWithPort.split(","))
            .filter(Predicate.not(String::isBlank))
            .map(h -> {
                final String[] hostToPort = h.split(":");
                return Map.entry(hostToPort[0], Integer.valueOf(hostToPort[1]));
            })
            .distinct()
            .sorted(Map.Entry.comparingByKey())
            .toList();
    }

    /**
     * Extracts the database name from a set of PostgreSQL connection URLs.
     *
     * @param pgUrls a set of PostgreSQL connection URLs; must contain at least one valid URL.
     * @return the database name extracted from the URLs.
     * @throws IllegalArgumentException if the connection string is invalid.
     */
    static String extractDatabaseName(final Set<String> pgUrls) {
        final String pgUrl = pgUrls.iterator().next();
        final int lastIndexOfSlash = pgUrl.lastIndexOf('/');
        final String dbNameWithParams = pgUrl.substring(lastIndexOfSlash);
        final int lastIndex = dbNameWithParams.lastIndexOf('?');
        if (lastIndex > 1) {
            return dbNameWithParams.substring(0, lastIndex);
        } else if (lastIndex == 1) {
            throw new IllegalArgumentException("pgUrls contains invalid connection string " + pgUrl);
        }
        return dbNameWithParams;
    }

    /**
     * Extracts all host and port pairs from a PostgreSQL connection URL.
     *
     * @param pgUrl the PostgreSQL connection URL; must be valid and non-blank.
     * @return a string containing all host and port pairs.
     */
    private static String extractAllHostsWithPort(final String pgUrl) {
        final int lastIndex = pgUrl.lastIndexOf('/');
        if (lastIndex >= URL_HEADER.length()) {
            return pgUrl.substring(URL_HEADER.length(), lastIndex);
        }
        return pgUrl.substring(URL_HEADER.length());
    }

    /**
     * Constructs a common (joint) PostgreSQL URL for a primary server.
     *
     * @param firstPgUrl  a first PostgreSQL connection URL.
     * @param secondPgUrl a second PostgreSQL connection URL.
     * @return the constructed primary connection URL.
     */
    public static String buildCommonUrlToPrimary(final String firstPgUrl,
                                                 final String secondPgUrl) {
        return buildCommonUrlToPrimary(Set.of(firstPgUrl, secondPgUrl));
    }

    /**
     * Constructs a common (joint) PostgreSQL URL for a primary server.
     *
     * @param firstPgUrl    a first PostgreSQL connection URL.
     * @param secondPgUrl   a second PostgreSQL connection URL.
     * @param urlParameters optional additional parameters to include in the URL.
     * @return the constructed primary connection URL.
     */
    public static String buildCommonUrlToPrimary(final String firstPgUrl,
                                                 final String secondPgUrl,
                                                 final Map<String, String> urlParameters) {
        return buildCommonUrlToPrimary(Set.of(firstPgUrl, secondPgUrl), urlParameters);
    }

    /**
     * Constructs a common (joint) PostgreSQL URL for a primary server.
     *
     * @param pgUrls a set of PostgreSQL connection URLs.
     * @return the constructed primary connection URL.
     */
    public static String buildCommonUrlToPrimary(final Set<String> pgUrls) {
        return buildCommonUrlToPrimary(pgUrls, Map.of());
    }

    /**
     * Constructs a common (joint) PostgreSQL URL for a primary server.
     *
     * @param pgUrls        a set of PostgreSQL connection URLs.
     * @param urlParameters optional additional parameters to include in the URL.
     * @return the constructed primary connection URL.
     */
    public static String buildCommonUrlToPrimary(final Set<String> pgUrls,
                                                 final Map<String, String> urlParameters) {
        final String additionalUrlParams = constructUrlParameters(urlParameters);
        return URL_HEADER + pgUrls.stream()
            .map(PgUrlParser::extractAllHostsWithPort)
            .sorted()
            .collect(Collectors.joining(",")) +
            extractDatabaseName(pgUrls) + additionalUrlParams;
    }

    /**
     * Constructs a query string from default and provided URL parameters.
     *
     * @param urlParameters a map of additional URL parameters.
     * @return the constructed query string, prefixed by {@code ?}.
     */
    static String constructUrlParameters(final Map<String, String> urlParameters) {
        final Map<String, String> jointUrlParameters = new TreeMap<>(urlParameters);
        DEFAULT_URL_PARAMETERS.forEach(jointUrlParameters::putIfAbsent);

        final String additionalParameters = jointUrlParameters.entrySet().stream()
            .map(e -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining("&"));
        return "?" + additionalParameters;
    }
}
