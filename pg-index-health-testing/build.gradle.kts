description = "pg-index-health-testing is an auxiliary library that allows you to run a PostgreSQL cluster in tests."

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    implementation(rootProject.libs.apache.commons.dbcp2)
    implementation(platform("org.testcontainers:testcontainers-bom:1.19.3"))
    implementation("org.testcontainers:testcontainers")
    implementation("org.testcontainers:postgresql")
    implementation(rootProject.libs.awaitility)

    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation("org.mockito:mockito-core")
    testImplementation(rootProject.libs.logback.classic)
    testRuntimeOnly(rootProject.libs.postgresql)
}

tasks {
    test {
        maxParallelForks = 1 // to increase tests stability
    }
}
