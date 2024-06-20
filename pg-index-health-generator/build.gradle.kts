plugins {
    id("java-library")
    id("pg-index-health.java-compilation")
    id("pg-index-health.java-conventions")
    id("pg-index-health.publish")
    id("pg-index-health.pitest")
    id("pg-index-health.forbidden-apis")
}

description = "pg-index-health-generator is an extension for generating database migrations in sql format based on pg-index-health diagnostics."

dependencies {
    api(project(":pg-index-health-model"))
    implementation(libs.slf4j.api)

    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation(libs.logback.classic)
}
