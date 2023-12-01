import info.solidsoft.gradle.pitest.PitestTask

plugins {
    id("pg-index-health.java-conventions")
    id("pg-index-health.publish")
    id("info.solidsoft.pitest")
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

    pitest(libs.pitest.dashboard.reporter)
}

pitest {
    junit5PluginVersion.set(libs.versions.pitest.junit5Plugin.get())
    pitestVersion.set(libs.versions.pitest.core.get())
    threads.set(4)
    if (System.getenv("STRYKER_DASHBOARD_API_KEY") != null) {
        outputFormats.set(setOf("stryker-dashboard"))
    } else {
        outputFormats.set(setOf("HTML"))
    }
    timestampedReports.set(false)
    mutationThreshold.set(98)
}

tasks.withType<PitestTask>().configureEach {
    mustRunAfter(tasks.test)
}

tasks.build {
    dependsOn("pitest")
}
