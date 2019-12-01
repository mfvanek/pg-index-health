/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

final class PgUrlParser {

    private PgUrlParser() {
        throw new UnsupportedOperationException();
    }

    // For example, jdbc:postgresql://host-1:6432/db_name
    static List<Pair<String, String>> extractNamesAndUrlsForEachHost(@Nonnull final String pgUrl) {
        final String urlHeader = "jdbc:postgresql://";
        final int lastIndex = pgUrl.lastIndexOf('/');
        final String dbName = pgUrl.substring(lastIndex);
        final String allHosts = pgUrl.substring(urlHeader.length(), lastIndex);
        return Arrays.stream(allHosts.split(","))
                .distinct()
                .sorted()
                .map(h -> Pair.of(h, urlHeader + h + dbName))
                .collect(Collectors.toList());
    }
}
