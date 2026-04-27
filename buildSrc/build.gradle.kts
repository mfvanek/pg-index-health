plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.36.0")
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:6.5.1")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:7.2.3.7755")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:5.1.0")
    implementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.19.0")
    implementation("org.gradle:test-retry-gradle-plugin:1.6.4")
    implementation(libs.forbiddenapis)
    implementation(libs.detekt)
    val kotlinVersion = "2.3.21"
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
}
