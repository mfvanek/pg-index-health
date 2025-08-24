/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class GenerateModuleVersionTask : DefaultTask() {

    @get:Input
    abstract val moduleGroup: Property<String>

    @get:Input
    abstract val moduleName: Property<String>

    @get:Input
    abstract val moduleVersion: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val versionJava = outputDir.get()
            .file("io/github/mfvanek/pg/model/jackson/generated/ModuleVersion.java")
            .asFile
        versionJava.parentFile.mkdirs()
        versionJava.writeText("""
            /*
             * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
             * https://github.com/mfvanek/pg-index-health
             *
             * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
             * that detects common anti-patterns and promotes best practices.
             *
             * Licensed under the Apache License 2.0
             */

            package io.github.mfvanek.pg.model.jackson.generated;

            import com.fasterxml.jackson.core.Version;

            @io.github.mfvanek.pg.model.annotations.ExcludeFromJacocoGeneratedReport
            public final class ModuleVersion {
                public static final Version VERSION = new Version(${moduleVersion.get().replace(".", ", ")}, null, "${moduleGroup.get()}", "${moduleName.get()}");
            }

        """.trimIndent())
    }
}
