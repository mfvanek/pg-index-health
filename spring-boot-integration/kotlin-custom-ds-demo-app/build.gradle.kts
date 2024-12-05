plugins {
    id("pg-index-health.kotlin-application")
    alias(libs.plugins.spring.boot.gradlePlugin)
    alias(libs.plugins.spring.dependency.management)
}

ext["commons-lang3.version"] = libs.versions.commons.lang3.get()
ext["assertj.version"] = libs.versions.assertj.get()
// ext["mockito.version"] = libs.versions.mockito.get()
ext["junit-jupiter.version"] = libs.versions.junit.get()

dependencies {
    implementation(platform(libs.testcontainers.bom))
    implementation("org.testcontainers:postgresql")
    implementation(libs.spring.boot.starter.data.jdbc)
    implementation("org.liquibase:liquibase-core:4.30.0")
    implementation("com.github.blagerweij:liquibase-sessionlock:1.6.9")

    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(project(":spring-boot-integration:pg-index-health-test-starter"))
}
