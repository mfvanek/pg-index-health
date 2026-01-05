/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring")
    id("pg-index-health.java-compilation")
    id("io.gitlab.arturbosch.detekt")
    id("pg-index-health.forbidden-apis")
}

private val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    versionCatalog.findLibrary("detekt-formatting").ifPresent {
        detektPlugins(it)
    }
    versionCatalog.findLibrary("detekt-libraries").ifPresent {
        detektPlugins(it)
    }
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
            freeCompilerArgs.add("-Xnullability-annotations=@org.jspecify.annotations:strict")
            jvmTarget = JvmTarget.JVM_17
        }
    }

    withType<Detekt>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

   named("detekt") {
        dependsOn(detektMain, detektTest)
    }
}

detekt {
    toolVersion = versionCatalog.findVersion("detekt").get().requiredVersion
    config.setFrom(file("${rootDir}/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}
