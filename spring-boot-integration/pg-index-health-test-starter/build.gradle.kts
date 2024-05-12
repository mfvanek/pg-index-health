plugins {
    id("java-library")
    id("pg-index-health.java-compilation")
    id("pg-index-health.java-conventions")
    id("pg-index-health.publish")
    id("pg-index-health.pitest")
}

description = "Spring Boot Starter for pg-index-health library"

dependencies {
    api(project(":pg-index-health"))
    implementation(libs.spring.boot.starter.root)
    annotationProcessor(libs.spring.boot.autoconfigure.processor)
    annotationProcessor(libs.spring.boot.configuration.processor)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.apache.commons.lang3)
    testImplementation(libs.postgresql)
}

checkstyle {
    configFile = file("../../config/checkstyle/checkstyle.xml")
}

pmd {
    ruleSetFiles = files("../../config/pmd/pmd.xml")
}

spotbugs {
    excludeFilter.set(file("../../config/spotbugs/exclude.xml"))
}
