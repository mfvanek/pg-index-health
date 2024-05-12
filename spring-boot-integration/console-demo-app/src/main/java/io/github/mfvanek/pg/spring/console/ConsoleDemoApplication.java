/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsoleDemoApplication implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleDemoApplication.class);

    public static void main(final String[] args) {
        SpringApplication.run(ConsoleDemoApplication.class, args);
    }

    @Override
    public void run(final String... args) {
        LOGGER.info("Executing: command line runner");
    }
}
