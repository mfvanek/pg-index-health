/*
 * Copyright (c) 2021-2024. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health-test-starter
 *
 * This file is a part of "pg-index-health-test-starter".
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.h2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class H2DemoApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoadsAndDoesNotContainPgIndexHealthBeans() {
        assertThat(applicationContext.getBean("dataSource"))
            .isInstanceOf(DataSource.class);

        assertThat(applicationContext.containsBean("pgConnection"))
            .isFalse();
    }
}
