/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.pitest")
}

description = "pg-index-health-model-jackson3-module is an extension for serializing and deserializing database objects to and from JSON format with Jackson 3."

dependencies {
    api(project(":pg-index-health-model"))
    implementation(platform(libs.jackson3.bom))
    implementation("tools.jackson.core:jackson-databind")

    testImplementation(libs.classgraph)
}

val generateModuleVersion = tasks.register<GenerateJackson3ModuleVersionTask>("generateJackson3ModuleVersion") {
    moduleGroup.set(project.group.toString())
    moduleName.set(project.name)
    moduleVersion.set(project.version.toString())
    outputDir.set(layout.buildDirectory.dir("generated/sources/version"))
}

sourceSets["main"].java.srcDir(generateModuleVersion.map { it.outputs.files })

tasks.named("compileJava") {
    dependsOn(generateModuleVersion)
}
