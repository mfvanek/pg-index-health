import info.solidsoft.gradle.pitest.PitestTask

plugins {
    id("info.solidsoft.pitest")
}

description = "pg-index-health-model is a set of common classes and interfaces for getting information about PostgreSQL database objects."

dependencies {
    testImplementation(rootProject.libs.equalsverifier)
    testImplementation(rootProject.libs.mockito.core)

    testFixturesImplementation(rootProject.libs.jsr305)

    pitest(rootProject.libs.pitest.dashboard.reporter)
}

pitest {
    junit5PluginVersion.set(rootProject.libs.versions.pitest.junit5Plugin.get())
    pitestVersion.set(rootProject.libs.versions.pitest.core.get())
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
