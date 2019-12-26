/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.connection;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PgUrlParserTest {

    @Test
    void extractNamesAndUrlsForEachHost() {
        final List<Pair<String, String>> extractResult = PgUrlParser.extractNameWithPortAndUrlForEachHost(
                "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        assertThat(extractResult, hasSize(4));
        assertThat(extractResult, containsInAnyOrder(
                Pair.of("host-1:6432", "jdbc:postgresql://host-1:6432/db_name?ssl=true&sslmode=require"),
                Pair.of("host-2:6432", "jdbc:postgresql://host-2:6432/db_name?ssl=true&sslmode=require"),
                Pair.of("host-3:6432", "jdbc:postgresql://host-3:6432/db_name?ssl=true&sslmode=require"),
                Pair.of("host-4:6432", "jdbc:postgresql://host-4:6432/db_name?ssl=true&sslmode=require")
        ));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void extractNamesAndUrlsForEachHostWithInvalidUrl() {
        assertThrows(NullPointerException.class, () -> PgUrlParser.extractNameWithPortAndUrlForEachHost(null));
        assertThrows(IllegalArgumentException.class, () -> PgUrlParser.extractNameWithPortAndUrlForEachHost(""));
        assertThrows(IllegalArgumentException.class, () -> PgUrlParser.extractNameWithPortAndUrlForEachHost("host-name:5432"));
    }

    @Test
    void extractNamesAndUrlsForMaster() {
        final List<Pair<String, String>> extractResult = PgUrlParser.extractNameWithPortAndUrlForEachHost(
                "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?targetServerType=master&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require");
        assertThat(extractResult, hasSize(4));
        assertThat(extractResult, containsInAnyOrder(
                Pair.of("host-1:6432", "jdbc:postgresql://host-1:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Pair.of("host-2:6432", "jdbc:postgresql://host-2:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Pair.of("host-3:6432", "jdbc:postgresql://host-3:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Pair.of("host-4:6432", "jdbc:postgresql://host-4:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require")
        ));
    }

    @Test
    void extractHostNames() {
        final Set<String> hostNames = PgUrlParser.extractHostNames(
                "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        assertThat(hostNames, hasSize(4));
        assertThat(hostNames, containsInAnyOrder("host-1", "host-2", "host-3", "host-4"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void extractHostNamesWithInvalidUrl() {
        assertThrows(NullPointerException.class, () -> PgUrlParser.extractHostNames(null));
        assertThrows(IllegalArgumentException.class, () -> PgUrlParser.extractHostNames(""));
        assertThrows(IllegalArgumentException.class, () -> PgUrlParser.extractHostNames("host-name:5432"));
    }
}
