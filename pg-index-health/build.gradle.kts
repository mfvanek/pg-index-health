description = "pg-index-health is a Java library for analyzing and maintaining indexes health in PostgreSQL databases."

dependencies {
    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    implementation(rootProject.libs.slf4j.api)

    testImplementation(project(":pg-index-health-testing"))
    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation(rootProject.libs.logback.classic)
    testImplementation("org.mockito:mockito-core")
    testImplementation(rootProject.libs.awaitility)
    testImplementation(rootProject.libs.apache.commons.lang3)
    testImplementation(rootProject.libs.postgresql)
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
