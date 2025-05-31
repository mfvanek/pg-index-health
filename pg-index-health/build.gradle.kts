plugins {
    id("pg-index-health.java-library")
}

description = "pg-index-health is a Java library for analyzing and maintaining indexes and tables health in PostgreSQL databases on all hosts in the cluster."

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    api(project(":pg-index-health-core"))

    testImplementation(project(":pg-index-health-testing"))
    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation(testFixtures(project(":pg-index-health-core")))
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.mockito:mockito-core")
    testImplementation(libs.awaitility)
    testImplementation(libs.postgresql)
}

tasks {
    test {
        useJUnitPlatform {
            excludeTags("cluster")
        }
    }
}

val clusterTest = tasks.register("clusterTest", Test::class) {
    description = "Runs tests on PostgreSQL cluster."
    group = "verification"
    useJUnitPlatform {
        includeTags("cluster")
    }
    maxParallelForks = 1 // important!
    mustRunAfter(tasks.test)
}

tasks.build {
    dependsOn(clusterTest)
}
