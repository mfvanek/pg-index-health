/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.h2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(OutputCaptureExtension.class)
class H2DemoApplicationRunTest {

    @Test
    void applicationShouldRun(final CapturedOutput output) {
        assertThatCode(() -> H2DemoApplication.main(new String[]{}))
            .doesNotThrowAnyException();
        assertThat(output.getAll())
            .contains("Starting H2DemoApplication using Java")
            .contains("Started H2DemoApplication in");
    }
}
