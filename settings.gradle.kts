rootProject.name = "pg-index-health-build"
include("pg-index-health-model")
include("pg-index-health")
include("pg-index-health-jdbc-connection")
include("pg-index-health-generator")
include("pg-index-health-testing")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("jsr305", "com.google.code.findbugs:jsr305:3.0.2")
            library("postgresql", "org.postgresql:postgresql:42.6.0")
            library("logback-classic", "ch.qos.logback:logback-classic:1.2.12")
            library("slf4j-api", "org.slf4j:slf4j-api:1.7.36") // to be compatible with Spring Boot 2.7.X
            library("apache-commons-dbcp2", "org.apache.commons:commons-dbcp2:2.10.0")
            library("mockito-core", "org.mockito:mockito-core:5.6.0")
            library("awaitility", "org.awaitility:awaitility:4.2.0")
            library("apache-commons-lang3", "org.apache.commons:commons-lang3:3.13.0")
            library("equalsverifier", "nl.jqno.equalsverifier:equalsverifier:3.15.2")
            library("pitest-dashboard-reporter", "it.mulders.stryker:pit-dashboard-reporter:0.2.1")
            version("pitest-junit5Plugin", "1.2.0")
            version("pitest-core", "1.15.0")
            version("checkstyle", "10.12.0")
            version("pmd", "6.55.0")
            version("jacoco", "0.8.10")
        }
    }
}
