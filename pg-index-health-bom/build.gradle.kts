/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

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
        api(project(":jackson-integration:pg-index-health-model-jackson2-module"))
        api(project(":jackson-integration:pg-index-health-model-jackson3-module"))
    }
}

mavenPublishing {
    configure(JavaPlatform())
}
