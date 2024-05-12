/*
 * Copyright (c) 2021-2024. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health-test-starter
 *
 * This file is a part of "pg-index-health-test-starter".
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.h2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class H2DemoApplication {

    /**
     * Demo application with H2 datasource and without PostgreSQL.
     */
    public static void main(final String[] args) {
        SpringApplication.run(H2DemoApplication.class, args);
    }
}
