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
    id("pg-index-health.pitest")
}

description = "pg-index-health-jdbc-connection is an abstraction of a connection to a high availability PostgreSQL cluster."

dependencies {
    api(project(":pg-index-health-model"))
    implementation(libs.apache.commons.dbcp2)
    implementation(libs.slf4j.api)

    testImplementation(project(":pg-index-health-testing"))
    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(libs.logback.classic)
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.equalsverifier)
    testImplementation(libs.awaitility)

    testRuntimeOnly(libs.postgresql)

    testFixturesImplementation(libs.jsr305)
    testFixturesImplementation(libs.slf4j.api)
    testFixturesImplementation(libs.logback.classic)
}

pitest {
    mutationThreshold.set(98)
}
