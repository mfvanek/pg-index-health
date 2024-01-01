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

description = "pg-index-health-model is a set of common classes and interfaces for getting information about PostgreSQL database objects."

dependencies {
    testImplementation(libs.equalsverifier)
    testImplementation("org.mockito:mockito-core")

    testFixturesImplementation(libs.jsr305)
}
