plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.mandatory-javadoc")
}

description = "pg-index-health-core is an embeddable schema linter for PostgreSQL that detects common anti-patterns and promotes best practices on a specific host."

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))

    testImplementation(project(":pg-index-health-testing"))
    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.postgresql)
    testImplementation(libs.slf4j.simple)
    testImplementation(libs.slf4j.jul)
    testImplementation(libs.arch.unit)

    testFixturesImplementation(libs.jspecify)
    testFixturesImplementation(libs.apache.commons.lang3)
    testFixturesImplementation(libs.postgresql)
    testFixturesImplementation(project(":pg-index-health-testing"))
    testFixturesImplementation(platform(libs.junit.bom))
    testFixturesImplementation("org.junit.platform:junit-platform-launcher")
    testFixturesImplementation(libs.slf4j.jul)
}
