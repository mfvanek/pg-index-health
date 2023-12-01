import info.solidsoft.gradle.pitest.PitestTask

plugins {
    id("pg-index-health.java-conventions")
    id("pg-index-health.publish")
    id("info.solidsoft.pitest")
}

description = "pg-index-health-generator is an extension for generating database migrations in sql format based on pg-index-health diagnostics."

dependencies {
    api(project(":pg-index-health-model"))
    implementation(libs.slf4j.api)

    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation(libs.logback.classic)

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
    mutationThreshold.set(96)
}

tasks.withType<PitestTask>().configureEach {
    mustRunAfter(tasks.test)
}

tasks.build {
    dependsOn("pitest")
}
