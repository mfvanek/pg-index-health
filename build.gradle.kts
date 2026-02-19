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
    version = "0.40.0"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

private val excludedSubprojects = setOf("pg-index-health-bom", "spring-boot-integration")

private fun Project.shouldSkip(): Boolean =
    excludedSubprojects.contains(this.name) || this.name.endsWith("-demo-app")

dependencies {
    subprojects.forEach { subproject ->
        if (!subproject.shouldSkip()) {
            jacocoAggregation(subproject)
        }
    }
}

tasks {
    wrapper {
        gradleVersion = "9.3.1"
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

gradle.projectsEvaluated {
    val groupId = rootProject.group
    val projectVersion = rootProject.version

    subprojects
        .filter { subproject -> subproject.plugins.hasPlugin(JavaLibraryPlugin::class.java) }
        .forEach { subproject ->
            logger.info("Configuring javadoc links for project $subproject")
            subproject.tasks.named<Javadoc>("javadoc").configure {
                subproject.configurations.findByName("api")?.dependencies
                    ?.filterIsInstance<ProjectDependency>()
                    ?.forEach { projectDependency ->
                        val dependencyProject = rootProject.project(projectDependency.path)
                        evaluationDependsOn(dependencyProject.path)
                        val link = "https://javadoc.io/doc/$groupId/${projectDependency.name}/$projectVersion/"
                        val javadocTask = dependencyProject.tasks.named<Javadoc>("javadoc")
                        val javadocOutputDir = javadocTask.get().destinationDir
                        val javadocAbsolutePath = javadocOutputDir?.absolutePath!!
                        logger.quiet("Adding offline link $link to $javadocAbsolutePath")
                        val doclet = options as StandardJavadocDocletOptions
                        doclet.linksOffline(link, javadocAbsolutePath)
                        dependsOn(javadocTask)
                    }
            }
        }
}
