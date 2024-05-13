plugins {
    id("pg-index-health.java-compilation")
    id("pg-index-health.java-conventions")
    alias(libs.plugins.spring.boot.gradlePlugin)
    alias(libs.plugins.spring.dependency.management)
    id("io.freefair.lombok") version "8.6"
}

ext["commons-lang3.version"] = libs.versions.commons.lang3.get()
ext["assertj.version"] = libs.versions.assertj.get()
ext["mockito.version"] = libs.versions.mockito.get()
ext["junit-jupiter.version"] = libs.versions.junit.get()

dependencies {
    implementation(project(":spring-boot-integration:pg-index-health-test-starter"))
    implementation(project(":pg-index-health-testing"))
    implementation(libs.spring.boot.starter.data.jdbc)
    implementation(platform(libs.testcontainers.bom))
    implementation("org.testcontainers:postgresql")

    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.starter.test)

    spotbugsSlf4j(libs.slf4j.simple)
}

lombok {
    version = "1.18.32"
}

checkstyle {
    configFile = file("../../config/checkstyle/checkstyle.xml")
}

pmd {
    ruleSetFiles = files("../../config/pmd/pmd.xml")
}

spotbugs {
    excludeFilter.set(file("../../config/spotbugs/exclude.xml"))
}
