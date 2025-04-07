plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.pitest")
}

description = "pg-index-health-model is a set of common classes and interfaces for getting information about PostgreSQL database objects."

dependencies {
    testImplementation(libs.equalsverifier)
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation(platform("com.fasterxml.jackson:jackson-bom:2.18.3"))
    testImplementation("com.fasterxml.jackson.core:jackson-databind")

    testFixturesImplementation(libs.jsr305)
    testFixturesCompileOnly(libs.forbiddenapis)
}
