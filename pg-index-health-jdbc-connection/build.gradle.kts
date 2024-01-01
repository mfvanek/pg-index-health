plugins {
    id("pg-index-health.java-conventions")
    id("pg-index-health.publish")
    id("pg-index-health.pitest")
}

description = "pg-index-health-jdbc-connection is an abstraction of a connection to a high availability PostgreSQL cluster."

dependencies {
    api(project(":pg-index-health-model"))
    implementation(libs.apache.commons.dbcp2)
    implementation(libs.slf4j.api)

    testImplementation(project(":pg-index-health-testing"))
    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(libs.logback.classic)
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.equalsverifier)
    testImplementation(libs.awaitility)

    testRuntimeOnly(libs.postgresql)

    testFixturesImplementation(libs.jsr305)
    testFixturesImplementation(libs.slf4j.api)
    testFixturesImplementation(libs.logback.classic)
}

pitest {
    mutationThreshold.set(98)
}
