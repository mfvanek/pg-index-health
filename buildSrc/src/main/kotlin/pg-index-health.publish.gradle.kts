/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("com.vanniktech.maven.publish")
    id("signing")
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.DEFAULT)
    signAllPublications()

    pom {
        name.set(project.name)
        description.set(project.provider(project::getDescription))
        inceptionYear.set("2019")
        url.set("https://github.com/mfvanek/pg-index-health")
        licenses {
            license {
                name.set("Apache License Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("mfvanek")
                name.set("Ivan Vakhrushev")
                email.set("mfvanek@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:https://github.com/mfvanek/pg-index-health.git")
            developerConnection.set("scm:git@github.com:mfvanek/pg-index-health.git")
            url.set("https://github.com/mfvanek/pg-index-health")
        }
    }
}

signing {
    if (!version.toString().endsWith("SNAPSHOT")) {
        useGpgCmd()
        sign(publishing.publications)
    }
}
