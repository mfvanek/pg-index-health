plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.pitest")
}

description = "pg-index-health-model-jackson-module is an extension for serializing and deserializing database objects to and from JSON format."

dependencies {
    api(project(":pg-index-health-model"))
    implementation(platform(libs.jackson.bom))
    implementation("com.fasterxml.jackson.core:jackson-databind")
}

val generateModuleVersion by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/sources/version")
    outputs.dir(outputDir)

    doLast {
        val versionJava = outputDir.get().file("io/github/mfvanek/pg/model/jackson/ModuleVersion.java").asFile
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

            package io.github.mfvanek.pg.model.jackson;

            import com.fasterxml.jackson.core.Version;

            public final class ModuleVersion {
                public static final Version VERSION = new Version(${project.version.toString().replace(".", ", ")}, null, "${project.group}", "${project.name}");
            }

        """.trimIndent())
    }
}

sourceSets["main"].java.srcDir(generateModuleVersion.map { it.outputs.files })

tasks.named("compileJava") {
    dependsOn(generateModuleVersion)
}
