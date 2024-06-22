plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.pitest")
}

description = "pg-index-health-model is a set of common classes and interfaces for getting information about PostgreSQL database objects."

dependencies {
    testImplementation(libs.equalsverifier)
    testImplementation("org.mockito:mockito-core")

    testFixturesImplementation(libs.jsr305)
    testFixturesCompileOnly(libs.forbiddenapis)
}
