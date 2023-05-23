import info.solidsoft.gradle.pitest.PitestTask

plugins {
    id("info.solidsoft.pitest")
}

description = "pg-index-health-jdbc-connection is an abstraction of a connection to a high availability PostgreSQL cluster."

dependencies {
    val slf4jVersion: String by rootProject.extra
    val logbackVersion: String by rootProject.extra
    val dbcp2Version: String by rootProject.extra
    val postgresqlVersion: String by rootProject.extra
    val mockitoVersion: String by rootProject.extra
    val awaitilityVersion: String by rootProject.extra
    val equalsverifierVersion: String by rootProject.extra
    val jsr305Version: String by rootProject.extra
    val pitDashboardReporterVersion: String by rootProject.extra

    api(project(":pg-index-health-model"))
    implementation("org.apache.commons:commons-dbcp2:$dbcp2Version")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    testImplementation(project(":pg-index-health-testing"))
    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("nl.jqno.equalsverifier:equalsverifier:$equalsverifierVersion")
    testImplementation("org.awaitility:awaitility:$awaitilityVersion")
    testRuntimeOnly("org.postgresql:postgresql:$postgresqlVersion")

    testFixturesImplementation("com.google.code.findbugs:jsr305:$jsr305Version")
    testFixturesImplementation("org.slf4j:slf4j-api:$slf4jVersion")
    testFixturesImplementation("ch.qos.logback:logback-classic:$logbackVersion")

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
    mutationThreshold.set(98)
}

tasks.withType<PitestTask>().configureEach {
    mustRunAfter(tasks.test)
}

tasks.build {
    dependsOn("pitest")
}
