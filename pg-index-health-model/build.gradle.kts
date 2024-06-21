plugins {
    id("java-library")
    id("pg-index-health.java-compilation")
    id("pg-index-health.java-conventions")
    id("pg-index-health.publish")
    id("pg-index-health.pitest")
    id("pg-index-health.forbidden-apis")
}

description = "pg-index-health-model is a set of common classes and interfaces for getting information about PostgreSQL database objects."

dependencies {
    testImplementation(libs.equalsverifier)
    testImplementation("org.mockito:mockito-core")

    testFixturesImplementation(libs.jsr305)
    testFixturesImplementation("de.thetaphi:forbiddenapis:3.1")
}
