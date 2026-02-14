/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import com.github.spotbugs.snom.SpotBugsTask
import net.ltgt.gradle.errorprone.errorprone
import org.sonarqube.gradle.SonarTask

plugins {
    id("java")
    id("org.sonarqube")
    id("checkstyle")
    id("pmd")
    id("com.github.spotbugs")
    id("jacoco")
    id("java-test-fixtures")
    id("net.ltgt.errorprone")
    id("org.gradle.test-retry")
}

dependencies {
    errorprone("com.google.errorprone:error_prone_core:2.47.0")
    errorprone("com.uber.nullaway:nullaway:0.13.1")

    spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.14.0")
    spotbugsPlugins("com.mebigfatguy.sb-contrib:sb-contrib:7.7.4")
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        disableWarningsInGeneratedCode.set(true)
        disable("StringSplitter", "ImmutableEnumChecker", "FutureReturnValueIgnored", "EqualsIncompatibleType", "TruthSelfEquals", "BooleanLiteral")
        option("NullAway:OnlyNullMarked", "true")
        error("NullAway")
        if (name.lowercase().contains("test")) {
            disable("NullAway")
        }
    }
}

tasks {
    test {
        dependsOn(checkstyleMain, checkstyleTest, checkstyleTestFixtures, pmdMain, pmdTest, pmdTestFixtures, spotbugsMain, spotbugsTest, spotbugsTestFixtures)
    }

    withType<Test>().configureEach {
        retry {
            maxRetries.set(2)
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
        val groupId = project.group
        val projectVersion = project.version
        val doclet = options as StandardJavadocDocletOptions
        doclet.addBooleanOption("html5", true)
        configurations.findByName("api")?.let { cfg ->
            cfg.dependencies
                .filterIsInstance<ProjectDependency>()
                .map {
                    val link = "https://javadoc.io/doc/$groupId/${it.name}/$projectVersion/"
                    val dependencyProject = rootProject.project(it.path)
                    val javadocTask = dependencyProject.tasks.named<Javadoc>("javadoc")
                    val javadocOutputDir = javadocTask.get().destinationDir
                    doclet.linksOffline(link, javadocOutputDir?.absolutePath)
                    dependsOn(javadocTask)
                }
        }
    }

    withType<SpotBugsTask>().configureEach {
        reports {
            create("xml") { required = true }
            create("html") { required = true }
        }
    }

    withType<SonarTask>().configureEach {
        dependsOn(test, jacocoTestReport)
    }
}

checkstyle {
    toolVersion = "13.2.0"
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
    maxErrors = 0
}

pmd {
    toolVersion = "7.21.0"
    isConsoleOutput = true
    ruleSetFiles = files("${rootDir}/config/pmd/pmd.xml")
    ruleSets = listOf()
}

spotbugs {
    toolVersion.set("4.9.8")
    showProgress.set(true)
    effort.set(Effort.MAX)
    reportLevel.set(Confidence.LOW)
    excludeFilter.set(file("${rootDir}/config/spotbugs/exclude.xml"))
}
