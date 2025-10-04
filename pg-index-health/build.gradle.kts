plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.mandatory-javadoc")
}

description = "pg-index-health is an embeddable schema linter for PostgreSQL that detects common anti-patterns and promotes best practices."

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    api(project(":pg-index-health-core"))

    testImplementation(project(":pg-index-health-testing"))
    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation(testFixtures(project(":pg-index-health-core")))
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.awaitility)
    testImplementation(libs.postgresql)
    testImplementation(libs.slf4j.simple)
    testImplementation(libs.slf4j.jul)
}
