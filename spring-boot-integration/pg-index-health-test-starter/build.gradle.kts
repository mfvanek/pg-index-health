plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.pitest")
}

description = "Spring Boot Starter for pg-index-health-core library"

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    api(project(":pg-index-health-core"))
    implementation(libs.spring.boot.starter.root)
    annotationProcessor(libs.spring.boot.autoconfigure.processor)
    annotationProcessor(libs.spring.boot.configuration.processor)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.apache.commons.lang3)
    testImplementation(libs.postgresql)
}
