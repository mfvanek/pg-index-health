import info.solidsoft.gradle.pitest.PitestTask

plugins {
    id("info.solidsoft.pitest")
}

description = "pg-index-health-jdbc-connection is an abstraction of a connection to a high availability PostgreSQL cluster."

dependencies {
    api(project(":pg-index-health-model"))
    implementation(rootProject.libs.apache.commons.dbcp2)
    implementation(rootProject.libs.slf4j.api)

    testImplementation(project(":pg-index-health-testing"))
    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(rootProject.libs.logback.classic)
    testImplementation(rootProject.libs.mockito.core)
    testImplementation(rootProject.libs.equalsverifier)
    testImplementation(rootProject.libs.awaitility)
    testRuntimeOnly(rootProject.libs.postgresql)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher") {
        because("required for pitest")
    }

    testFixturesImplementation(rootProject.libs.jsr305)
    testFixturesImplementation(rootProject.libs.slf4j.api)
    testFixturesImplementation(rootProject.libs.logback.classic)

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
    mutationThreshold.set(98)
}

tasks.withType<PitestTask>().configureEach {
    mustRunAfter(tasks.test)
}

tasks.build {
    dependsOn("pitest")
}
