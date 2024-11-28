plugins {
    id("pg-index-health.java-application")
    alias(libs.plugins.spring.boot.gradlePlugin)
    alias(libs.plugins.spring.dependency.management)
}

ext["commons-lang3.version"] = libs.versions.commons.lang3.get()
ext["assertj.version"] = libs.versions.assertj.get()
// ext["mockito.version"] = libs.versions.mockito.get()
ext["junit-jupiter.version"] = libs.versions.junit.get()

dependencies {
    implementation(libs.spring.boot.starter.data.jdbc)

    runtimeOnly("com.h2database:h2")

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(project(":spring-boot-integration:pg-index-health-test-starter"))
}
