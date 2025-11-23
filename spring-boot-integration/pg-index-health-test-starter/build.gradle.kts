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
    id("pg-index-health.java-library")
    id("pg-index-health.pitest")
}

description = "pg-index-health-test-starter is a Spring Boot Starter for pg-index-health-core library"

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    api(project(":pg-index-health-core"))

    implementation(libs.spring.boot.v3.starter.root)

    annotationProcessor(libs.spring.boot.v3.autoconfigure.processor)
    annotationProcessor(libs.spring.boot.v3.configuration.processor)

    testImplementation(libs.spring.boot.v3.starter.test)
    testImplementation(libs.apache.commons.lang3)
    testImplementation(libs.postgresql)
}
