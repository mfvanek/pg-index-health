description = "pg-index-health is a Java library for analyzing and maintaining indexes health in PostgreSQL databases."

dependencies {
    val slf4jVersion: String by rootProject.extra
    val logbackVersion: String by rootProject.extra
    val postgresqlVersion: String by rootProject.extra
    val mockitoVersion: String by rootProject.extra
    val awaitilityVersion: String by rootProject.extra
    val commonsLang3Version: String by rootProject.extra

    api(project(":pg-index-health-model"))
    api(project(":pg-index-health-jdbc-connection"))
    implementation("org.slf4j:slf4j-api:$slf4jVersion")

    testImplementation(project(":pg-index-health-testing"))
    testImplementation(testFixtures(project(":pg-index-health-model")))
    testImplementation(testFixtures(project(":pg-index-health-jdbc-connection")))
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.awaitility:awaitility:$awaitilityVersion")
    testImplementation("org.apache.commons:commons-lang3:$commonsLang3Version")
    testImplementation("org.postgresql:postgresql:$postgresqlVersion")
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
