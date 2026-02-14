/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

plugins {
    id("java")
    id("jacoco")
}

configurations.compileClasspath {
    exclude("com.google.code.findbugs", "jsr305")
}

private val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    versionCatalog.findLibrary("jspecify").ifPresent {
        implementation(it)
    }

    versionCatalog.findVersion("assertj").ifPresent {
        testImplementation(platform("org.assertj:assertj-bom:$it"))
    }
    testImplementation("org.assertj:assertj-core")

    versionCatalog.findVersion("mockito").ifPresent {
        testImplementation(platform("org.mockito:mockito-bom:$it"))
    }
    versionCatalog.findLibrary("junit-bom").ifPresent {
        testImplementation(platform(it))
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    // Remember about Kotlin app
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-parameters")
        options.compilerArgs.add("--should-stop=ifError=FLOW")
    }

    test {
        testLogging.showStandardStreams = false // set to true for debug purposes
        useJUnitPlatform()
        maxParallelForks = 1 // try to set a higher value to speed up the local build
        finalizedBy(jacocoTestReport, jacocoTestCoverageVerification)
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
}

jacoco {
    toolVersion = "0.8.14"
}
