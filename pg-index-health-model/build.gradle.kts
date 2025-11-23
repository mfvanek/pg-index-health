plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.pitest")
    id("pg-index-health.mandatory-javadoc")
}

description = "pg-index-health-model is a set of common classes and interfaces for getting information about PostgreSQL database objects."

dependencies {
    testImplementation(libs.equalsverifier)
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.arch.unit)

    testFixturesImplementation(libs.jspecify)
    testFixturesCompileOnly(libs.forbiddenapis)
}
