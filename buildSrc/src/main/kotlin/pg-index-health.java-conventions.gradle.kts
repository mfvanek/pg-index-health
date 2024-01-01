import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import net.ltgt.gradle.errorprone.errorprone
import org.sonarqube.gradle.SonarTask

plugins {
    id("java-library")
    id("org.sonarqube")
    id("checkstyle")
    id("pmd")
    id("com.github.spotbugs")
    id("jacoco")
    id("java-test-fixtures")
    id("net.ltgt.errorprone")
    id("org.gradle.test-retry")
}

val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    versionCatalog.findLibrary("jsr305").ifPresent {
        implementation(it)
    }

    testImplementation("org.assertj:assertj-core:3.25.0")
    testImplementation(platform("org.mockito:mockito-bom:5.8.0"))
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    checkstyle("com.thomasjensen.checkstyle.addons:checkstyle-addons:7.0.1")
    errorprone("com.google.errorprone:error_prone_core:2.24.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withJavadocJar()
    withSourcesJar()
}
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        disable("StringSplitter", "ImmutableEnumChecker", "FutureReturnValueIgnored", "EqualsIncompatibleType", "TruthSelfEquals")
    }
}

jacoco {
    toolVersion = "0.8.11"
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
    toolVersion = "10.12.7"
    configFile = file("../config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
    maxErrors = 0
}

pmd {
    toolVersion = "6.55.0"
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
