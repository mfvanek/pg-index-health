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

import io.github.mfvanek.pg.utils.TestUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgUrlParserTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(PgUrlParser.class)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void extractNamesAndUrlsForEachHost() {
        final List<Pair<String, String>> extractResult = PgUrlParser.extractNameWithPortAndUrlForEachHost(
                "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        assertThat(extractResult).hasSize(4);
        assertThat(extractResult).containsExactlyInAnyOrder(
                Pair.of("host-1:6432", "jdbc:postgresql://host-1:6432/db_name?ssl=true&sslmode=require"),
                Pair.of("host-2:6432", "jdbc:postgresql://host-2:6432/db_name?ssl=true&sslmode=require"),
                Pair.of("host-3:6432", "jdbc:postgresql://host-3:6432/db_name?ssl=true&sslmode=require"),
                Pair.of("host-4:6432", "jdbc:postgresql://host-4:6432/db_name?ssl=true&sslmode=require")
        );
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void extractNamesAndUrlsForEachHostWithInvalidUrl() {
        assertThatThrownBy(() -> PgUrlParser.extractNameWithPortAndUrlForEachHost(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> PgUrlParser.extractNameWithPortAndUrlForEachHost("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PgUrlParser.extractNameWithPortAndUrlForEachHost("host-name:5432")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void extractNamesAndUrlsForDeprecatedMaster() {
        final List<Pair<String, String>> extractResult = PgUrlParser.extractNameWithPortAndUrlForEachHost(
                "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?targetServerType=master&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require");
        assertThat(extractResult).hasSize(4);
        assertThat(extractResult).containsExactlyInAnyOrder(
                Pair.of("host-1:6432", "jdbc:postgresql://host-1:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Pair.of("host-2:6432", "jdbc:postgresql://host-2:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Pair.of("host-3:6432", "jdbc:postgresql://host-3:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Pair.of("host-4:6432", "jdbc:postgresql://host-4:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require")
        );
    }

    @Test
    void extractNamesAndUrlsForPrimary() {
        final List<Pair<String, String>> extractResult = PgUrlParser.extractNameWithPortAndUrlForEachHost(
                "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?targetServerType=primary&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require");
        assertThat(extractResult).hasSize(4);
        assertThat(extractResult).containsExactlyInAnyOrder(
                Pair.of("host-1:6432", "jdbc:postgresql://host-1:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Pair.of("host-2:6432", "jdbc:postgresql://host-2:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Pair.of("host-3:6432", "jdbc:postgresql://host-3:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Pair.of("host-4:6432", "jdbc:postgresql://host-4:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require")
        );
    }

    @Test
    void extractHostNames() {
        final Set<String> hostNames = PgUrlParser.extractHostNames(
                "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        assertThat(hostNames).hasSize(4);
        assertThat(hostNames).containsExactlyInAnyOrder("host-1", "host-2", "host-3", "host-4");
    }

    @Test
    void extractHostNamesWithIncompleteUrl() {
        final Set<String> hostNames = PgUrlParser.extractHostNames(
                "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432");
        assertThat(hostNames).hasSize(4);
        assertThat(hostNames).containsExactlyInAnyOrder("host-1", "host-2", "host-3", "host-4");
    }

    @Test
    void extractHostNamesWithEmptyUrl() {
        final Set<String> hostNames = PgUrlParser.extractHostNames("jdbc:postgresql://");
        assertThat(hostNames).isEmpty();
    }

    @Test
    void extractHostNamesWithBadUrl() {
        final Set<String> hostNames = PgUrlParser.extractHostNames("jdbc:postgresql:///");
        assertThat(hostNames).isEmpty();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void extractHostNamesWithInvalidUrl() {
        assertThatThrownBy(() -> PgUrlParser.extractHostNames(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> PgUrlParser.extractHostNames("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PgUrlParser.extractHostNames("host-name:5432")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PgUrlParser.extractHostNames("jdbc:postgresql:/")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isReplicaUrl() {
        assertThat(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=primary&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require")).isFalse();
        assertThat(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=master&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require")).isFalse();
        assertThat(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require")).isFalse();
        assertThat(
                PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=preferSlave&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require")).isFalse();
        assertThat(PgUrlParser.isReplicaUrl(
                "jdbc:postgresql://host-1:6432/db_name?targetServerType=preferSecondary&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require")).isFalse();
        assertThat(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=slave&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require")).isTrue();
        assertThat(PgUrlParser.isReplicaUrl("jdbc:postgresql://host-1:6432/db_name?targetServerType=secondary&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require")).isTrue();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void isReplicaUrlWithInvalidUrl() {
        assertThatThrownBy(() -> PgUrlParser.isReplicaUrl(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> PgUrlParser.isReplicaUrl("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> PgUrlParser.isReplicaUrl("host-name:5432")).isInstanceOf(IllegalArgumentException.class);
    }
}
