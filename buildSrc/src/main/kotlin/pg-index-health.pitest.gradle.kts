import info.solidsoft.gradle.pitest.PitestTask

plugins {
    id("java-library")
    id("info.solidsoft.pitest")
}

dependencies {
    pitest("it.mulders.stryker:pit-dashboard-reporter:0.2.1")
}

pitest {
    junit5PluginVersion.set("1.2.1")
    pitestVersion.set("1.15.8")
    threads.set(4)
    if (System.getenv("STRYKER_DASHBOARD_API_KEY") != null) {
        outputFormats.set(setOf("stryker-dashboard"))
    } else {
        outputFormats.set(setOf("HTML"))
    }
    timestampedReports.set(false)
    mutationThreshold.set(100)
    exportLineCoverage.set(true)
    pluginConfiguration.set(mapOf("stryker.moduleName" to project.name))
}

tasks.withType<PitestTask>().configureEach {
    mustRunAfter(tasks.test)
}

tasks.build {
    dependsOn("pitest")
}
