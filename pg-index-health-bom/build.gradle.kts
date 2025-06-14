import com.vanniktech.maven.publish.JavaPlatform

plugins {
    id("java-platform")
    id("pg-index-health.publish")
}

description = "pg-index-health library BOM"

dependencies {
    constraints {
        api(project(":pg-index-health-model"))
        api(project(":pg-index-health"))
        api(project(":pg-index-health-jdbc-connection"))
        api(project(":pg-index-health-generator"))
        api(project(":pg-index-health-testing"))
        api(project(":spring-boot-integration:pg-index-health-test-starter"))
        api(project(":pg-index-health-logger"))
        api(project(":pg-index-health-core"))
    }
}

mavenPublishing {
    configure(JavaPlatform())
}
