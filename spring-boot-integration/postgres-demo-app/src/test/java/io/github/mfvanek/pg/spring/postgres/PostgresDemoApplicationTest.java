/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres;

import com.zaxxer.hikari.HikariDataSource;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class PostgresDemoApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Autowired
    private List<DatabaseCheckOnHost<? extends DbObject>> checks;

    @Test
    void contextLoadsAndDoesNotContainPgIndexHealthBeans() {
        assertThat(applicationContext.getBean("dataSource"))
            .isInstanceOf(HikariDataSource.class);

        assertThat(applicationContext.getBean("pgConnection"))
            .isInstanceOf(PgConnection.class);

        assertThat(environment.getProperty("spring.datasource.url"))
            .isNotBlank()
            .startsWith("jdbc:postgresql://localhost:")
            .endsWith("/demo_for_pg_index_health_starter?loggerLevel=OFF");
    }

    @Test
    void checksShouldWork() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());

        checks.stream()
            .filter(DatabaseCheckOnHost::isStatic)
            .forEach(c -> assertThat(c.check())
                .as(c.getDiagnostic().name())
                .isEmpty());
    }
}
