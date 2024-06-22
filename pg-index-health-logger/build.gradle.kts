plugins {
    id("pg-index-health.java-library")
}

description = "pg-index-health-logger is a Java library for collecting and logging health state in PostgreSQL databases."

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    api(project(":pg-index-health-core"))
    api(project(":pg-index-health"))
    implementation(libs.slf4j.api)

    testImplementation(testFixtures(project(":pg-index-health-core")))
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation(libs.logback.classic)
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.postgresql)

    testCompileOnly(libs.forbiddenapis)
}
