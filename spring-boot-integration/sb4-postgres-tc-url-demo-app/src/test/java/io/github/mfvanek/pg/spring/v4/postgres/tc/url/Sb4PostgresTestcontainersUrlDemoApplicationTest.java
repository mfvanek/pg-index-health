/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.v4.postgres.tc.url;

import com.zaxxer.hikari.HikariDataSource;
import io.github.mfvanek.pg.connection.PgConnection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class Sb4PostgresTestcontainersUrlDemoApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Test
    void contextLoadsAndContainsPgIndexHealthBeans() {
        assertThat(applicationContext.getBean("dataSource"))
            .isInstanceOf(HikariDataSource.class);

        assertThat(applicationContext.getBean("pgConnection"))
            .isInstanceOf(PgConnection.class);

        assertThat(environment.getProperty("spring.datasource.url"))
            .isNotBlank()
            .isEqualTo("jdbc:tc:postgresql:18.1:///demo_for_pg_index_health_starter");
    }
}
