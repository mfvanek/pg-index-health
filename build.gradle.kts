import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("java")
    id("jacoco-report-aggregation")
    id("org.sonarqube")
    id("com.github.ben-manes.versions") version "0.53.0"
}

description = "pg-index-health build"

allprojects {
    group = "io.github.mfvanek"
    version = "0.30.0"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

dependencies {
    val excludedSubprojects = setOf("pg-index-health-bom", "spring-boot-integration")
    subprojects.forEach {
        val shouldSkip = excludedSubprojects.contains(it.name) || it.name.endsWith("-demo-app")
        if (!shouldSkip) {
            jacocoAggregation(it)
        }
    }
}

tasks{
    wrapper {
        gradleVersion = "9.1.0"
    }

    check {
        dependsOn(named<JacocoReport>("testCodeCoverageReport"))
    }

    // To avoid creation of jar's in build folder in the root
    jar {
        isEnabled = false
    }
}

sonar {
    properties {
        property("sonar.projectKey", "mfvanek_pg-index-health")
        property("sonar.organization", "mfvanek")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.exclusions", "**/build.gradle.kts")
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    checkForGradleUpdate = true
    gradleReleaseChannel = "current"
    checkConstraints = true
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}
