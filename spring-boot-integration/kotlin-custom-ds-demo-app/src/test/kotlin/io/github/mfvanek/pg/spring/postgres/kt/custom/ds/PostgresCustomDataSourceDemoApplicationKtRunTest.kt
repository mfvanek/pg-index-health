package io.github.mfvanek.pg.spring.postgres.kt.custom.ds

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension

@ExtendWith(OutputCaptureExtension::class)
internal class PostgresCustomDataSourceDemoApplicationKtRunTest {

    @Test
    fun applicationShouldRun(output: CapturedOutput) {
        assertThatCode { main(arrayOf("--spring.profiles.active=test")) }
            .doesNotThrowAnyException()
        assertThat(output.all)
            .contains("Reading from custom_ds_schema.databasechangelog")
            .contains("Starting PostgresCustomDataSourceDemoApplicationKt using Java")
            .contains("Started PostgresCustomDataSourceDemoApplicationKt in")
    }
}
