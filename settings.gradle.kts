rootProject.name = "pg-index-health-build"

include("pg-index-health-model")
include("pg-index-health")
include("pg-index-health-jdbc-connection")
include("pg-index-health-generator")
include("pg-index-health-testing")
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
include("pg-index-health-bom")
include("pg-index-health-logger")
include("pg-index-health-core")
include("spring-boot-integration:postgres-tc-url-demo-app")
findProject(":spring-boot-integration:postgres-tc-url-demo-app")?.name = "postgres-tc-url-demo-app"
include("spring-boot-integration:kotlin-custom-ds-demo-app")
findProject(":spring-boot-integration:kotlin-custom-ds-demo-app")?.name = "kotlin-custom-ds-demo-app"
