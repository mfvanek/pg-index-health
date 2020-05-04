/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.utils.TestUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PgUrlParserTest {

    @Test
    void privateConstructor() {
        assertThrows(UnsupportedOperationException.class, () -> TestUtils.invokePrivateConstructor(PgUrlParser.class));
    }

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
    void extractNamesAndUrlsForDeprecatedMaster() {
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
    void extractNamesAndUrlsForPrimary() {
        final List<Pair<String, String>> extractResult = PgUrlParser.extractNameWithPortAndUrlForEachHost(
                "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?targetServerType=primary&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require");
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

    @Test
    void extractHostNamesWithIncompleteUrl() {
        final Set<String> hostNames = PgUrlParser.extractHostNames(
                "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432");
        assertThat(hostNames, hasSize(4));
        assertThat(hostNames, containsInAnyOrder("host-1", "host-2", "host-3", "host-4"));
    }

    @Test
    void extractHostNamesWithEmptyUrl() {
        final Set<String> hostNames = PgUrlParser.extractHostNames("jdbc:postgresql://");
        assertThat(hostNames, empty());
    }

    @Test
    void extractHostNamesWithBadUrl() {
        final Set<String> hostNames = PgUrlParser.extractHostNames("jdbc:postgresql:///");
        assertThat(hostNames, empty());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void extractHostNamesWithInvalidUrl() {
        assertThrows(NullPointerException.class, () -> PgUrlParser.extractHostNames(null));
        assertThrows(IllegalArgumentException.class, () -> PgUrlParser.extractHostNames(""));
        assertThrows(IllegalArgumentException.class, () -> PgUrlParser.extractHostNames("host-name:5432"));
        assertThrows(IllegalArgumentException.class, () -> PgUrlParser.extractHostNames("jdbc:postgresql:/"));
    }

    @Test
    void isReplicaUrl() {
        assertFalse(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=primary&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"));
        assertFalse(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=master&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"));
        assertFalse(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"));
        assertFalse(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=preferSlave&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"));
        assertFalse(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=preferSecondary&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"));
        assertTrue(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=slave&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"));
        assertTrue(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=secondary&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void isReplicaUrlWithInvalidUrl() {
        assertThrows(NullPointerException.class, () -> PgUrlParser.isReplicaUrl(null));
        assertThrows(IllegalArgumentException.class, () -> PgUrlParser.isReplicaUrl(""));
        assertThrows(IllegalArgumentException.class, () -> PgUrlParser.isReplicaUrl("host-name:5432"));
    }
}
