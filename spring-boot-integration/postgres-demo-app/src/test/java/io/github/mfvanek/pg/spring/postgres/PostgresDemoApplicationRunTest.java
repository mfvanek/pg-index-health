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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(OutputCaptureExtension.class)
class PostgresDemoApplicationRunTest {

    @Test
    void applicationShouldRun(final CapturedOutput output) {
        assertThatCode(() -> PostgresDemoApplication.main(new String[]{}))
            .doesNotThrowAnyException();
        assertThat(output.getAll())
            .contains("Starting PostgresDemoApplication using Java")
            .contains("Container is started (JDBC URL: jdbc:postgresql://localhost:")
            .contains("Started PostgresDemoApplication in");
    }
}
