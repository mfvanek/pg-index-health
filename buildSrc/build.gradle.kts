plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("net.ltgt.errorprone:net.ltgt.errorprone.gradle.plugin:3.0.1")
    implementation("com.github.spotbugs:com.github.spotbugs.gradle.plugin:5.0.14")
    implementation("org.sonarqube:org.sonarqube.gradle.plugin:4.2.0.3129")
    implementation("info.solidsoft.pitest:info.solidsoft.pitest.gradle.plugin:1.9.11")
    implementation("org.gradle.test-retry:org.gradle.test-retry.gradle.plugin:1.5.3")
}
