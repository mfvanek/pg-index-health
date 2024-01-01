/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

plugins {
    id("pg-index-health.java-conventions")
    id("pg-index-health.publish")
}

description = "pg-index-health-testing is an auxiliary library that allows you to run a PostgreSQL cluster in tests."

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    implementation(libs.apache.commons.dbcp2)
    implementation(platform("org.testcontainers:testcontainers-bom:1.19.3"))
    implementation("org.testcontainers:testcontainers")
    implementation("org.testcontainers:postgresql")
    implementation(libs.awaitility)

    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.logback.classic)
    testRuntimeOnly(libs.postgresql)
}

tasks {
    test {
        maxParallelForks = 1 // to increase tests stability
    }
}
