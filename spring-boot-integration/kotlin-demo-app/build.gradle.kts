/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

plugins {
    id("pg-index-health.kotlin-application")
    alias(libs.plugins.spring.boot.v3.gradle.plugin)
    alias(libs.plugins.spring.dependency.management)
}

ext["commons-lang3.version"] = libs.versions.commons.lang3.get()
ext["assertj.version"] = libs.versions.assertj.get()
ext["junit-jupiter.version"] = libs.versions.junit.get()

dependencies {
    implementation(project(":pg-index-health-testing"))
    implementation(libs.spring.boot.v3.starter.jdbc)
    implementation(platform(libs.testcontainers.bom))
    implementation("org.testcontainers:postgresql")

    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.v3.starter.test)
    testImplementation(project(":spring-boot-integration:pg-index-health-test-starter"))
}
