import info.solidsoft.gradle.pitest.PitestTask

plugins {
    id("info.solidsoft.pitest")
}

description = "pg-index-health-generator is an extension for generating database migrations in sql format based on pg-index-health diagnostics."

dependencies {
    val slf4jVersion: String by rootProject.extra
    val logbackVersion: String by rootProject.extra
    val pitDashboardReporterVersion: String by rootProject.extra

    api(project(":pg-index-health-model"))
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")

    pitest("it.mulders.stryker:pit-dashboard-reporter:$pitDashboardReporterVersion")
}

pitest {
    junit5PluginVersion.set("1.1.2")
    pitestVersion.set("1.10.4")
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
