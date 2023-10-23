import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import net.ltgt.gradle.errorprone.errorprone
import org.sonarqube.gradle.SonarTask

plugins {
    id("java")
    id("maven-publish")
    id("signing")
    id("jacoco")
    id("jacoco-report-aggregation")
    id("checkstyle")
    id("pmd")
    id("java-test-fixtures")
    id("net.ltgt.errorprone") version "3.1.0"
    id("com.github.spotbugs") version "5.1.4"
    id("org.sonarqube") version "4.4.1.3373"
    id("info.solidsoft.pitest") version "1.15.0"
    id("org.gradle.test-retry") version "1.5.6"
    id("com.github.ben-manes.versions") version "0.49.0"
}

description = "pg-index-health build"

allprojects {
    group = "io.github.mfvanek"
    version = "0.10.1"

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.sonarqube")
    apply(plugin = "checkstyle")
    apply(plugin = "pmd")
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "java-test-fixtures")
    apply(plugin = "net.ltgt.errorprone")
    apply(plugin = "org.gradle.test-retry")

    dependencies {
        implementation(rootProject.libs.jsr305)

        testImplementation("org.assertj:assertj-core:3.24.2")
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
        testImplementation("org.junit.jupiter:junit-jupiter-api")

        checkstyle("com.thomasjensen.checkstyle.addons:checkstyle-addons:7.0.1")
        errorprone("com.google.errorprone:error_prone_core:2.23.0")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        withJavadocJar()
        withSourcesJar()
    }
    tasks.withType<JavaCompile>().configureEach {
        options.errorprone {
            disableWarningsInGeneratedCode.set(true)
            disable("StringSplitter", "ImmutableEnumChecker", "FutureReturnValueIgnored", "EqualsIncompatibleType")
        }
    }

    jacoco {
        toolVersion = rootProject.libs.versions.jacoco.get()
    }

    tasks {
        test {
            testLogging.showStandardStreams = false // set to true for debug purposes
            useJUnitPlatform()
            dependsOn(checkstyleMain, checkstyleTest, pmdMain, pmdTest, spotbugsMain, spotbugsTest)
            maxParallelForks = 1 // try to set a higher value to speed up the local build
            finalizedBy(jacocoTestReport, jacocoTestCoverageVerification)
        }

        withType<Test>().configureEach {
            retry {
                maxRetries.set(3)
                maxFailures.set(10)
                failOnPassedAfterRetry.set(false)
            }
        }

        jar {
            manifest {
                attributes["Implementation-Title"] = project.name
                attributes["Implementation-Version"] = project.version
            }
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
        }

        javadoc {
            if (JavaVersion.current().isJava9Compatible) {
                (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
            }
        }

        jacocoTestReport {
            dependsOn(test)
            reports {
                xml.required.set(true)
                html.required.set(true)
            }
        }

        jacocoTestCoverageVerification {
            dependsOn(jacocoTestReport)
            violationRules {
                rule {
                    limit {
                        counter = "CLASS"
                        value = "MISSEDCOUNT"
                        maximum = "0.0".toBigDecimal()
                    }
                }
                rule {
                    limit {
                        counter = "METHOD"
                        value = "MISSEDCOUNT"
                        maximum = "0.0".toBigDecimal()
                    }
                }
                rule {
                    limit {
                        counter = "LINE"
                        value = "MISSEDCOUNT"
                        maximum = "0.0".toBigDecimal()
                    }
                }
                rule {
                    limit {
                        counter = "INSTRUCTION"
                        value = "COVEREDRATIO"
                        minimum = "1.0".toBigDecimal()
                    }
                }
                rule {
                    limit {
                        counter = "BRANCH"
                        value = "COVEREDRATIO"
                        minimum = "1.0".toBigDecimal()
                    }
                }
            }
        }

        check {
            dependsOn(jacocoTestCoverageVerification)
        }

        withType<SpotBugsTask>().configureEach {
            reports {
                create("xml") { enabled = true }
                create("html") { enabled = true }
            }
        }

        withType<SonarTask>().configureEach {
            dependsOn(test, jacocoTestReport)
        }
    }

    checkstyle {
        toolVersion = rootProject.libs.versions.checkstyle.get()
        configFile = file("../config/checkstyle/checkstyle.xml")
        isIgnoreFailures = false
        maxWarnings = 0
        maxErrors = 0
    }

    pmd {
        toolVersion = rootProject.libs.versions.pmd.get()
        isConsoleOutput = true
        ruleSetFiles = files("../config/pmd/pmd.xml")
        ruleSets = listOf()
    }

    spotbugs {
        showProgress.set(true)
        effort.set(Effort.MAX)
        reportLevel.set(Confidence.LOW)
        excludeFilter.set(file("../config/spotbugs/exclude.xml"))
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                suppressPomMetadataWarningsFor("testFixturesApiElements")
                suppressPomMetadataWarningsFor("testFixturesRuntimeElements")
                versionMapping {
                    usage("java-api") {
                        fromResolutionOf("runtimeClasspath")
                    }
                    usage("java-runtime") {
                        fromResolutionResult()
                    }
                }

                pom {
                    name.set(project.name)
                    description.set(project.provider(project::getDescription))
                    url.set("https://github.com/mfvanek/pg-index-health")
                    licenses {
                        license {
                            name.set("Apache License Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }

                    developers {
                        developer {
                            id.set("mfvanek")
                            name.set("Ivan Vakhrushev")
                            email.set("mfvanek@gmail.com")
                        }
                        developer {
                            id.set("Evreke")
                            name.set("Alex Antipin")
                            email.set("evreke@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/mfvanek/pg-index-health.git")
                        developerConnection.set("scm:git@github.com:mfvanek/pg-index-health.git")
                        url.set("https://github.com/mfvanek/pg-index-health")
                    }
                }
            }
        }

        repositories {
            maven {
                val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                val sonatypeUsername: String by project
                val sonatypePassword: String by project
                credentials {
                    username = sonatypeUsername
                    password = sonatypePassword
                }
            }
        }
    }

    signing {
        if (!version.toString().endsWith("SNAPSHOT")) {
            useGpgCmd()
            sign(publishing.publications["mavenJava"])
        }
    }
}

dependencies {
    subprojects.forEach {
        jacocoAggregation(it)
    }
}

tasks.check {
    dependsOn(tasks.named<JacocoReport>("testCodeCoverageReport"))
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

// To avoid creation of jar's in build folder in the root
tasks {
    jar {
        isEnabled = false
    }
    testFixturesJar {
        isEnabled = false
    }
}
