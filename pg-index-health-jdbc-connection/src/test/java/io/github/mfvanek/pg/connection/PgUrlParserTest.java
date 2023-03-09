/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

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
                .hasMessage("pgUrl cannot be blank or empty");
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
                .hasMessage("pgUrl cannot be blank or empty");
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
                .hasMessage("pgUrl cannot be blank or empty");
        assertThatThrownBy(() -> PgUrlParser.isReplicaUrl("host-name:5432"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pgUrl has invalid format");
    }
}
