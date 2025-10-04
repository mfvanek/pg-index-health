plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.mandatory-javadoc")
}

description = "pg-index-health-logger is a Java library for collecting and logging schema health state in PostgreSQL databases."

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    api(project(":pg-index-health-core"))
    api(project(":pg-index-health"))

    testImplementation(testFixtures(project(":pg-index-health-core")))
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.postgresql)
    testImplementation(libs.slf4j.simple)
    testImplementation(libs.slf4j.jul)

    testCompileOnly(libs.forbiddenapis)
}
