/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.checks.custom;

import io.github.mfvanek.pg.connection.PgConnection;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;

@TestConfiguration(proxyBeanMethods = false)
public class CustomChecksConfig {

    @Bean
    AllDateTimeColumnsShouldEndWithAtCheckOnHost allDateTimeColumnsShouldEndWithAtCheckOnHost(final PgConnection pgConnection) {
        return new AllDateTimeColumnsShouldEndWithAtCheckOnHost(pgConnection);
    }

    @Bean
    AllPrimaryKeysMustBeNamedAsIdCheckOnHost allPrimaryKeysMustBeNamedAsIdCheckOnHost(final PgConnection pgConnection,
                                                                                      final JdbcClient jdbcClient) {
        return new AllPrimaryKeysMustBeNamedAsIdCheckOnHost(pgConnection, jdbcClient);
    }
}
