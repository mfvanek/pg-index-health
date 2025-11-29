/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

import info.solidsoft.gradle.pitest.PitestTask

plugins {
    id("java")
    id("info.solidsoft.pitest")
}

dependencies {
    pitest("it.mulders.stryker:pit-dashboard-reporter:0.3.10")
}

pitest {
    junit5PluginVersion.set("1.2.3")
    pitestVersion.set("1.22.0")
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
