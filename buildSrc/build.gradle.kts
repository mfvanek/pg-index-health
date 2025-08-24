plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.34.0")
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:6.2.5")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:6.2.0.5505")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:4.3.0")
    implementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.15.0")
    implementation("org.gradle:test-retry-gradle-plugin:1.6.2")
    implementation(libs.forbiddenapis)
    implementation(libs.detekt)
    val kotlinVersion = "2.0.21"
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
}
