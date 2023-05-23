description = "pg-index-health-testing is an auxiliary library that allows you to run a PostgreSQL cluster in tests."

dependencies {
    val logbackVersion: String by rootProject.extra
    val dbcp2Version: String by rootProject.extra
    val testcontainersVersion: String by rootProject.extra
    val postgresqlVersion: String by rootProject.extra
    val mockitoVersion: String by rootProject.extra
    val awaitilityVersion: String by rootProject.extra

    api(project(":pg-index-health-model"))
    implementation("org.apache.commons:commons-dbcp2:$dbcp2Version")
    implementation("org.testcontainers:testcontainers:$testcontainersVersion")
    implementation("org.testcontainers:postgresql:$testcontainersVersion")
    implementation("org.awaitility:awaitility:$awaitilityVersion")

    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")
    testRuntimeOnly("org.postgresql:postgresql:$postgresqlVersion")
}

tasks {
    test {
        maxParallelForks = 1 // to increase tests stability
    }
}
