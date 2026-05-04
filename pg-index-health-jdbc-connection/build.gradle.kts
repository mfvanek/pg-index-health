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
    id("pg-index-health.mandatory-javadoc")
}

description = "pg-index-health-jdbc-connection is an abstraction of a connection to a high-availability PostgreSQL cluster."

dependencies {
    api(project(":pg-index-health-model"))
    implementation(libs.apache.commons.dbcp2)

    testImplementation(project(":pg-index-health-testing"))
    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.equalsverifier)
    testImplementation(libs.awaitility)

    testRuntimeOnly(libs.postgresql)

    testFixturesImplementation(libs.jspecify)
}
