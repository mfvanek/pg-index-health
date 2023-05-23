import info.solidsoft.gradle.pitest.PitestTask

plugins {
    id("info.solidsoft.pitest")
}

description = "pg-index-health-model is a set of common classes and interfaces for getting information about PostgreSQL database objects."

dependencies {
    val mockitoVersion: String by rootProject.extra
    val equalsverifierVersion: String by rootProject.extra
    val jsr305Version: String by rootProject.extra
    val pitDashboardReporterVersion: String by rootProject.extra

    testImplementation("nl.jqno.equalsverifier:equalsverifier:$equalsverifierVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")

    testFixturesImplementation("com.google.code.findbugs:jsr305:$jsr305Version")

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
    mutationThreshold.set(100)
}

tasks.withType<PitestTask>().configureEach {
    mustRunAfter(tasks.test)
}

tasks.build {
    dependsOn("pitest")
}
