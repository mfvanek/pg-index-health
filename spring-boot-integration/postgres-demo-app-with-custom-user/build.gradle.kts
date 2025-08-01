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
    id("pg-index-health.java-application")
    alias(libs.plugins.spring.boot.gradlePlugin)
    alias(libs.plugins.spring.dependency.management)
    id("io.freefair.lombok") version "8.14"
}

ext["commons-lang3.version"] = libs.versions.commons.lang3.get()
ext["assertj.version"] = libs.versions.assertj.get()
ext["junit-jupiter.version"] = libs.versions.junit.get()

dependencies {
    implementation(project(":pg-index-health-testing"))
    implementation(libs.spring.boot.starter.data.jdbc)
    implementation(platform(libs.testcontainers.bom))
    implementation("org.testcontainers:postgresql")
    implementation(libs.liquibase.core)
    implementation(libs.liquibase.sessionlock)

    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(project(":spring-boot-integration:pg-index-health-test-starter"))
}
