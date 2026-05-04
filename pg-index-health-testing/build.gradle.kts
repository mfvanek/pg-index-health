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
    //id("pg-index-health.mandatory-javadoc") temporarily disabled due to JPMS support
}

description = "pg-index-health-testing is an auxiliary library that allows you to run a PostgreSQL in tests."

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    implementation(libs.apache.commons.dbcp2)
    implementation(platform(libs.testcontainers.bom))
    implementation("org.testcontainers:testcontainers")
    implementation("org.testcontainers:testcontainers-jdbc")
    implementation("org.testcontainers:testcontainers-postgresql")
    implementation(libs.awaitility)

    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation("org.mockito:mockito-core")
    testRuntimeOnly(libs.postgresql)
}

tasks {
    test {
        maxParallelForks = 1 // to increase tests stability
    }

    jar {
        manifest {
            attributes("Automatic-Module-Name" to "io.github.mfvanek.pg.testing")
        }
    }
}
