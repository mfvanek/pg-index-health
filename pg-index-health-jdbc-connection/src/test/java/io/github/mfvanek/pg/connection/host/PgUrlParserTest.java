/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.host;

import io.github.mfvanek.pg.model.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class PgUrlParserTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(PgUrlParser.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void extractNamesAndUrlsForEachHost() {
        assertThat(PgUrlParser.extractNameWithPortAndUrlForEachHost(
            "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require"))
            .hasSize(4)
            .containsExactlyInAnyOrder(
                Map.entry("host-1:6432", "jdbc:postgresql://host-1:6432/db_name?ssl=true&sslmode=require"),
                Map.entry("host-2:6432", "jdbc:postgresql://host-2:6432/db_name?ssl=true&sslmode=require"),
                Map.entry("host-3:6432", "jdbc:postgresql://host-3:6432/db_name?ssl=true&sslmode=require"),
                Map.entry("host-4:6432", "jdbc:postgresql://host-4:6432/db_name?ssl=true&sslmode=require"))
            .isUnmodifiable();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void extractNamesAndUrlsForEachHostWithInvalidUrl() {
        assertThatThrownBy(() -> PgUrlParser.extractNameWithPortAndUrlForEachHost(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgUrl cannot be null");
        assertThatThrownBy(() -> PgUrlParser.extractNameWithPortAndUrlForEachHost(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrl cannot be blank");
        assertThatThrownBy(() -> PgUrlParser.extractNameWithPortAndUrlForEachHost("host-name:5432"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrl has invalid format");
    }

    @Test
    void extractNamesAndUrlsForDeprecatedMaster() {
        assertThat(PgUrlParser.extractNameWithPortAndUrlForEachHost(
            "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?targetServerType=master&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"))
            .hasSize(4)
            .containsExactlyInAnyOrder(
                Map.entry("host-1:6432", "jdbc:postgresql://host-1:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Map.entry("host-2:6432", "jdbc:postgresql://host-2:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Map.entry("host-3:6432", "jdbc:postgresql://host-3:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Map.entry("host-4:6432", "jdbc:postgresql://host-4:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"))
            .isUnmodifiable();
    }

    @Test
    void extractNamesAndUrlsForPrimary() {
        assertThat(PgUrlParser.extractNameWithPortAndUrlForEachHost(
            "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?targetServerType=primary&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"))
            .hasSize(4)
            .containsExactlyInAnyOrder(
                Map.entry("host-1:6432", "jdbc:postgresql://host-1:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Map.entry("host-2:6432", "jdbc:postgresql://host-2:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Map.entry("host-3:6432", "jdbc:postgresql://host-3:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"),
                Map.entry("host-4:6432", "jdbc:postgresql://host-4:6432/db_name?targetServerType=any&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require"))
            .isUnmodifiable();
    }

    @Test
    void extractHostNames() {
        assertThat(PgUrlParser.extractHostNames("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require"))
            .hasSize(4)
            .containsExactly(Map.entry("host-1", 6432), Map.entry("host-2", 6432), Map.entry("host-3", 6432), Map.entry("host-4", 6432))
            .isUnmodifiable();
    }

    @Test
    void extractHostNamesWithIncompleteUrl() {
        assertThat(PgUrlParser.extractHostNames("jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432"))
            .hasSize(4)
            .containsExactly(Map.entry("host-1", 6432), Map.entry("host-2", 6432), Map.entry("host-3", 6432), Map.entry("host-4", 6432))
            .isUnmodifiable();
    }

    @Test
    void extractHostNamesWithDuplicatesInUrl() {
        assertThat(PgUrlParser.extractHostNames("jdbc:postgresql://host-1:6432,host-1:6432,host-1:6432,host-2:6432"))
            .hasSize(2)
            .containsExactly(Map.entry("host-1", 6432), Map.entry("host-2", 6432))
            .isUnmodifiable();
    }

    @Test
    void extractHostNamesWithEmptyUrl() {
        assertThat(PgUrlParser.extractHostNames("jdbc:postgresql://"))
            .isUnmodifiable()
            .isEmpty();
    }

    @Test
    void extractHostNamesWithBadUrl() {
        assertThat(PgUrlParser.extractHostNames("jdbc:postgresql:///"))
            .isUnmodifiable()
            .isEmpty();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void extractHostNamesWithInvalidUrl() {
        assertThatThrownBy(() -> PgUrlParser.extractHostNames(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgUrl cannot be null");
        assertThatThrownBy(() -> PgUrlParser.extractHostNames(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrl cannot be blank");
        assertThatThrownBy(() -> PgUrlParser.extractHostNames("host-name:5432"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrl has invalid format");
        assertThatThrownBy(() -> PgUrlParser.extractHostNames("jdbc:postgresql:/"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrl has invalid format");
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
        assertThatThrownBy(() -> PgUrlParser.isReplicaUrl(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgUrl cannot be null");
        assertThatThrownBy(() -> PgUrlParser.isReplicaUrl(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrl cannot be blank");
        assertThatThrownBy(() -> PgUrlParser.isReplicaUrl("host-name:5432"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrl has invalid format");
    }

    @Test
    void extractDatabaseNameShouldWork() {
        assertThat(PgUrlParser.extractDatabaseName(Set.of("jdbc:postgresql://host:5432/db1?param=1")))
            .isEqualTo("/db1");

        assertThat(PgUrlParser.extractDatabaseName(Set.of("jdbc:postgresql://host:5432/db2?")))
            .isEqualTo("/db2");

        assertThat(PgUrlParser.extractDatabaseName(Set.of("jdbc:postgresql://host:5432/db3")))
            .isEqualTo("/db3");

        final Set<String> pgUrls = Set.of("jdbc:postgresql://host:5432/?");
        assertThatThrownBy(() -> PgUrlParser.extractDatabaseName(pgUrls))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgUrls contains invalid connection string jdbc:postgresql://host:5432/?");
    }

    @Test
    void constructUrlParametersShouldAddDefaultValues() {
        assertThat(PgUrlParser.constructUrlParameters(Map.of()))
            .isEqualTo("?connectTimeout=1&hostRecheckSeconds=2&socketTimeout=600&targetServerType=primary");

        assertThat(PgUrlParser.constructUrlParameters(
            Map.ofEntries(
                Map.entry("connectTimeout", "10"))))
            .isEqualTo("?connectTimeout=10&hostRecheckSeconds=2&socketTimeout=600&targetServerType=primary");

        assertThat(PgUrlParser.constructUrlParameters(
            Map.ofEntries(
                Map.entry("targetServerType", "any"),
                Map.entry("readOnly", "true"))))
            .isEqualTo("?connectTimeout=1&hostRecheckSeconds=2&readOnly=true&socketTimeout=600&targetServerType=any");
    }

    @Test
    void buildCommonUrlToPrimaryWithTwoUrls() {
        assertThat(PgUrlParser.buildCommonUrlToPrimary("jdbc:postgresql://host:5432/db1", "jdbc:postgresql://host:6432/db1"))
            .isEqualTo("jdbc:postgresql://host:5432,host:6432/db1?connectTimeout=1&hostRecheckSeconds=2&socketTimeout=600&targetServerType=primary");

        assertThat(PgUrlParser.buildCommonUrlToPrimary("jdbc:postgresql://host:5432/db1", "jdbc:postgresql://host:6432/db1",
            Map.ofEntries(
                Map.entry("targetServerType", "secondary"),
                Map.entry("readOnly", "true"))))
            .isEqualTo("jdbc:postgresql://host:5432,host:6432/db1?connectTimeout=1&hostRecheckSeconds=2&readOnly=true&socketTimeout=600&targetServerType=secondary");
    }

    @Test
    void buildCommonUrlToPrimaryWithSeveralUrls() {
        assertThat(PgUrlParser.buildCommonUrlToPrimary(Set.of("jdbc:postgresql://host2:5432/db1", "jdbc:postgresql://host:5432/db1", "jdbc:postgresql://host:6432/db1")))
            .isEqualTo("jdbc:postgresql://host2:5432,host:5432,host:6432/db1?connectTimeout=1&hostRecheckSeconds=2&socketTimeout=600&targetServerType=primary");

        assertThat(PgUrlParser.buildCommonUrlToPrimary(Set.of("jdbc:postgresql://host2:5432/db1", "jdbc:postgresql://host:5432/db1", "jdbc:postgresql://host:6432/db1"),
            Map.ofEntries(
                Map.entry("targetServerType", "secondary"),
                Map.entry("readOnly", "true"))))
            .isEqualTo("jdbc:postgresql://host2:5432,host:5432,host:6432/db1?connectTimeout=1&hostRecheckSeconds=2&readOnly=true&socketTimeout=600&targetServerType=secondary");
    }
}
