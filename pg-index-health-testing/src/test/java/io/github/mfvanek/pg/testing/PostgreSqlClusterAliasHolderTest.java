/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class PostgreSqlClusterAliasHolderTest {

    @Test
    void primaryAndStandbyNamesShouldDiffer() {
        final PostgreSqlClusterAliasHolder aliases = new PostgreSqlClusterAliasHolder();
        assertThat(aliases)
                .isNotNull()
                .satisfies(a -> {
                    assertThat(a.getPrimaryAlias())
                            .startsWith("pg-")
                            .endsWith("-0");
                    assertThat(a.getStandbyAlias())
                            .endsWith("-1")
                            .startsWith(a.getPrimaryAlias().substring(0, a.getPrimaryAlias().length() - 1));
                });
    }

    @Test
    void shouldCreateEnvMaps() {
        final PostgreSqlClusterAliasHolder aliases = new PostgreSqlClusterAliasHolder();
        final var builder = PostgreSqlClusterWrapper.builder()
                .withUsername("username")
                .withPassword("any#pwd")
                .withDatabaseName("test_db");
        assertThat(aliases)
                .isNotNull()
                .satisfies(a -> assertThat(a.createPrimaryEnvVarsMap(builder))
                        .hasSize(14)
                        .hasSameSizeAs(a.createStandbyEnvVarsMap(builder)));
    }
}
