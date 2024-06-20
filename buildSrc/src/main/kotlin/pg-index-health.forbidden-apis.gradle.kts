import de.thetaphi.forbiddenapis.gradle.CheckForbiddenApis

plugins {
    id("java-library")
    id("de.thetaphi.forbiddenapis")
}

dependencies {
    implementation("de.thetaphi:forbiddenapis:3.7")
}

forbiddenApis {
    bundledSignatures = setOf("jdk-unsafe", "jdk-deprecated", "jdk-internal", "jdk-non-portable", "jdk-system-out", "jdk-reflection")
    signaturesFiles = files("${rootDir}/config/forbidden-apis/forbidden-apis.txt")
    ignoreFailures = false
}

tasks.withType<CheckForbiddenApis>().configureEach {
    mustRunAfter(tasks.test)
}

tasks.build {
    dependsOn("forbiddenApis")
}
