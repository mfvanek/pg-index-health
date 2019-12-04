/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

// TODO actualize tests
final class PgUrlParser {

    private static final String URL_HEADER = "jdbc:postgresql://";

    private PgUrlParser() {
        throw new UnsupportedOperationException();
    }

    // For example, jdbc:postgresql://host-1:6432/db_name?param=value
    @Nonnull
    static List<Pair<String, String>> extractNamesAndUrlsForEachHost(@Nonnull final String pgUrl) {
        final int lastIndex = pgUrl.lastIndexOf('/');
        final String dbNameWithParams = pgUrl.substring(lastIndex);
        final String allHostsWithPort = extractAllHostsWithPort(pgUrl);
        return Arrays.stream(allHostsWithPort.split(","))
                .distinct()
                .sorted()
                .map(h -> Pair.of(h, URL_HEADER + h + dbNameWithParams))
                .collect(Collectors.toList());
    }

    @Nonnull
    static Set<String> extractHostNames(@Nonnull final String pgUrl) {
        final String allHostsWithPort = extractAllHostsWithPort(pgUrl);
        return Arrays.stream(allHostsWithPort.split(","))
                .filter(not(String::isBlank))
                .map(h -> h.substring(0, h.lastIndexOf(':')))
                .collect(Collectors.toSet());
    }

    @Nonnull
    private static String extractAllHostsWithPort(@Nonnull final String pgUrl) {
        final int lastIndex = pgUrl.lastIndexOf('/');
        return pgUrl.substring(URL_HEADER.length(), lastIndex);
    }
}
