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

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostgreSqlContainerWrapperTest {

    @Test
    void shouldWork() {
        try (PostgreSqlContainerWrapper container = new PostgreSqlContainerWrapper()) {
            assertThat(container)
                    .isNotNull();
            assertThat(container.getDataSource())
                    .isNotNull()
                    .isInstanceOf(BasicDataSource.class);
            assertThat(container.getPort())
                    .isPositive();
            assertThat(container.getUrl())
                    .startsWith("jdbc:postgresql://");
            assertThat(container.getUsername())
                    .isNotBlank();
            assertThat(container.getPassword())
                    .isNotBlank();
        }
    }
}
