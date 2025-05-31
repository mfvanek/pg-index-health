plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.pitest")
}

description = "pg-index-health-jdbc-connection is an abstraction of a connection to a high availability PostgreSQL cluster."

dependencies {
    api(project(":pg-index-health-model"))
    implementation(libs.apache.commons.dbcp2)

    testImplementation(project(":pg-index-health-testing"))
    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.equalsverifier)
    testImplementation(libs.awaitility)

    testRuntimeOnly(libs.postgresql)

    testFixturesImplementation(libs.jspecify)
}
