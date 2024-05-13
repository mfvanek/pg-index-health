/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.kt

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension

@ExtendWith(OutputCaptureExtension::class)
class PostgresDemoApplicationRunKtTest {

    @Test
    fun applicationShouldRun(output: CapturedOutput) {
        assertThatCode { main(arrayOf()) }
            .doesNotThrowAnyException()
        assertThat(output.all)
            .contains("Starting PostgresDemoApplicationKt using Java")
            .contains("Container is started (JDBC URL: jdbc:postgresql://localhost:")
            .contains("Started PostgresDemoApplicationKt in")
    }
}
