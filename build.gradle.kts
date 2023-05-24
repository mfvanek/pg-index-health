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
    id("com.github.spotbugs") version "5.0.14"
    id("org.sonarqube") version "4.0.0.2929"
    id("info.solidsoft.pitest") version "1.9.11"
    id("org.gradle.test-retry") version "1.5.2"
}

description = "pg-index-health build"

allprojects {
    group = "io.github.mfvanek"
    version = "0.9.2"

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

val slf4jVersion by extra { "2.0.7" }
val logbackVersion by extra { "1.4.7" }
val dbcp2Version by extra { "2.9.0" }
val testcontainersVersion by extra { "1.18.1" }
val postgresqlVersion by extra { "42.6.0" }
val mockitoVersion by extra { "5.3.1" }
val awaitilityVersion by extra { "4.2.0" }
val equalsverifierVersion by extra { "3.14.1" }
val commonsLang3Version by extra { "3.12.0" }
val jsr305Version by extra { "3.0.2" }
val pitDashboardReporterVersion by extra { "0.1.5" }

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
        implementation("com.google.code.findbugs:jsr305:$jsr305Version")

        testImplementation("org.assertj:assertj-core:3.24.2")
        testImplementation(enforcedPlatform("org.junit:junit-bom:5.9.3"))
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
        testImplementation("org.junit.jupiter:junit-jupiter-api")

        checkstyle("com.thomasjensen.checkstyle.addons:checkstyle-addons:7.0.1")
        errorprone("com.google.errorprone:error_prone_core:2.19.1")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        withJavadocJar()
        withSourcesJar()
    }

    tasks {
        withType<JavaCompile>().configureEach {
            options.errorprone {
                disableWarningsInGeneratedCode.set(true)
                disable("StringSplitter", "ImmutableEnumChecker", "FutureReturnValueIgnored", "EqualsIncompatibleType")
            }
        }

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
            dependsOn(test)
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
            dependsOn(jacocoTestReport, jacocoTestCoverageVerification)
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
        toolVersion = "10.5.0"
        configFile = file("../config/checkstyle/checkstyle.xml")
        isIgnoreFailures = false
        maxWarnings = 0
        maxErrors = 0
    }

    pmd {
        isConsoleOutput = true
        toolVersion = "6.52.0"
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

sonarqube {
    properties {
        property("sonar.projectKey", "mfvanek_pg-index-health")
        property("sonar.organization", "mfvanek")
        property("sonar.host.url", "https://sonarcloud.io")
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
