plugins {
    id("maven-publish")
    id("signing")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            suppressPomMetadataWarningsFor("testFixturesApiElements")
            suppressPomMetadataWarningsFor("testFixturesRuntimeElements")
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set(project.name)
                description.set(project.provider(project::getDescription))
                url.set("https://github.com/mfvanek/pg-index-health")
                licenses {
                    license {
                        name.set("Apache License Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("mfvanek")
                        name.set("Ivan Vakhrushev")
                        email.set("mfvanek@gmail.com")
                    }
                    developer {
                        id.set("Evreke")
                        name.set("Alex Antipin")
                        email.set("evreke@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/mfvanek/pg-index-health.git")
                    developerConnection.set("scm:git@github.com:mfvanek/pg-index-health.git")
                    url.set("https://github.com/mfvanek/pg-index-health")
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            val sonatypeUsername: String by project
            val sonatypePassword: String by project
            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
}

signing {
    if (!version.toString().endsWith("SNAPSHOT")) {
        useGpgCmd()
        sign(publishing.publications["mavenJava"])
    }
}
