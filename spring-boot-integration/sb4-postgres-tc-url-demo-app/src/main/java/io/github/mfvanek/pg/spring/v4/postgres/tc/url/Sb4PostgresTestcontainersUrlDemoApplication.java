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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Sb4PostgresTestcontainersUrlDemoApplication {

    /**
     * Demo application with PostgreSQL datasource.
     */
    public static void main(final String[] args) {
        SpringApplication.run(Sb4PostgresTestcontainersUrlDemoApplication.class, args);
    }
}
