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
    id("pg-index-health.mandatory-javadoc")
}

description = "pg-index-health-logger is a Java library for collecting and logging schema health state in PostgreSQL databases."

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    api(project(":pg-index-health-core"))
    api(project(":pg-index-health"))

    testImplementation(testFixtures(project(":pg-index-health-core")))
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.postgresql)
    testImplementation(libs.slf4j.simple)
    testImplementation(libs.slf4j.jul)

    testCompileOnly(libs.forbiddenapis)
}
