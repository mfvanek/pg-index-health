plugins {
    id("pg-index-health.java-library")
}

description = "pg-index-health-testing is an auxiliary library that allows you to run a PostgreSQL cluster in tests."

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    implementation(libs.apache.commons.dbcp2)
    implementation(platform(libs.testcontainers.bom))
    implementation("org.testcontainers:testcontainers")
    implementation("org.testcontainers:postgresql")
    implementation(libs.awaitility)

    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.logback.classic)
    testRuntimeOnly(libs.postgresql)
}

tasks {
    test {
        maxParallelForks = 1 // to increase tests stability
    }
}
