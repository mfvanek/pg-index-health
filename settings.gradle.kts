/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

rootProject.name = "pg-index-health-build"

include("pg-index-health-model")
include("pg-index-health")
include("pg-index-health-jdbc-connection")
include("pg-index-health-generator")
include("pg-index-health-testing")
include("pg-index-health-bom")
include("pg-index-health-logger")
include("pg-index-health-core")

include("spring-boot-integration:pg-index-health-test-starter")
findProject(":spring-boot-integration:pg-index-health-test-starter")?.name = "pg-index-health-test-starter"
include("spring-boot-integration:h2-demo-app")
findProject(":spring-boot-integration:h2-demo-app")?.name = "h2-demo-app"
include("spring-boot-integration:console-demo-app")
findProject(":spring-boot-integration:console-demo-app")?.name = "console-demo-app"
include("spring-boot-integration:postgres-demo-app")
findProject(":spring-boot-integration:postgres-demo-app")?.name = "postgres-demo-app"
include("spring-boot-integration:kotlin-demo-app")
findProject(":spring-boot-integration:kotlin-demo-app")?.name = "kotlin-demo-app"
include("spring-boot-integration:postgres-tc-url-demo-app")
findProject(":spring-boot-integration:postgres-tc-url-demo-app")?.name = "postgres-tc-url-demo-app"
include("spring-boot-integration:kotlin-custom-ds-demo-app")
findProject(":spring-boot-integration:kotlin-custom-ds-demo-app")?.name = "kotlin-custom-ds-demo-app"
include("spring-boot-integration:postgres-demo-app-with-custom-user")
findProject(":spring-boot-integration:postgres-demo-app-with-custom-user")?.name = "postgres-demo-app-with-custom-user"
include("spring-boot-integration:sb4-postgres-tc-url-demo-app")
findProject(":spring-boot-integration:sb4-postgres-tc-url-demo-app")?.name = "sb4-postgres-tc-url-demo-app"
include("spring-boot-integration:sb4-jackson3-demo-app")
findProject(":spring-boot-integration:sb4-jackson3-demo-app")?.name = "sb4-jackson3-demo-app"

include("jackson-integration:pg-index-health-model-jackson2-module")
findProject(":jackson-integration:pg-index-health-model-jackson2-module")?.name = "pg-index-health-model-jackson2-module"
include("jackson-integration:pg-index-health-model-jackson3-module")
findProject(":jackson-integration:pg-index-health-model-jackson3-module")?.name = "pg-index-health-model-jackson3-module"
