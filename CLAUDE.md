# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this project is

**pg-index-health** is an embeddable Java library and schema linter for PostgreSQL. It detects common anti-patterns in database schemas (missing indexes, bloat, bad column types, naming issues, etc.) by querying `pg_catalog`. It ships as a multi-module Gradle project with Spring Boot integration, a CLI/demo layer, and support for both Jackson 2 and Jackson 3.

## Build and test commands

Docker must be running — tests use Testcontainers (PostgreSQL in Docker).

```bash
# Full build (tests + all quality checks)
.\gradlew.bat build                              # Windows
./gradlew build                                  # Linux/macOS

# Build without tests
.\gradlew.bat build -x test

# Run all tests
.\gradlew.bat test

# Run tests in one module
.\gradlew.bat :pg-index-health-model:test

# Run a specific test class or method
.\gradlew.bat test --tests "*IndexesCheckOnHostTest"
.\gradlew.bat test --tests "*IndexesCheckOnHostTest.someMethod"

# Change PostgreSQL version (default: 18.1)
$env:TEST_PG_VERSION="17.6-alpine"; .\gradlew.bat build

# Mutation tests
.\gradlew.bat pitest

# Verify Javadoc compiles
.\gradlew.bat javadoc

# Update the SQL submodule after upstream SQL changes
git submodule foreach --recursive git pull origin master
```

## Module structure

| Module | Role |
|---|---|
| `pg-index-health-model` | Domain classes (indexes, tables, columns, etc.). Null-annotated with JSpecify. |
| `pg-index-health-core` | Host-level checks — each extends `AbstractCheckOnHost`. JDBC + `pg_catalog` queries. |
| `pg-index-health` | Cluster-level checks — each extends `AbstractCheckOnCluster`. Aggregates host checks. |
| `pg-index-health-jdbc-connection` | JDBC abstraction for HA clusters (wraps Apache DBCP2). |
| `pg-index-health-generator` | Generates corrective SQL migrations from check results. |
| `pg-index-health-testing` | Testcontainers-based test fixtures shared across modules. |
| `pg-index-health-logger` | Logging utilities. |
| `pg-index-health-bom` | Bill of Materials for downstream dependency management. |
| `jackson-integration/pg-index-health-model-jackson2-module` | Jackson 2 serializers/deserializers (Spring Boot 3). |
| `jackson-integration/pg-index-health-model-jackson3-module` | Jackson 3 serializers/deserializers (Spring Boot 4). |
| `spring-boot-integration/pg-index-health-test-starter` | Spring Boot auto-configuration starter. |

SQL queries live in a **git submodule** (`pg-index-health-sql`) pointing to a separate repository. Clone with `--recursive`.

## Implementing a new check (required steps)

1. **Add SQL** — write the query in the `pg-index-health-sql` submodule repo; merge it, then update the submodule here.
2. **Extend the domain model** (if needed) in `pg-index-health-model`. All domain classes need: a builder, JSpecify null annotations, and Jackson serializer/deserializer in both jackson modules.
3. **Host-level check** — create a class extending `AbstractCheckOnHost<T>` in `pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/host/`. Use `NamedParametersParser.parse()` for named SQL params, and one of the standard extractors in `pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/extractors/`.
4. **Cluster-level check** — create a class extending `AbstractCheckOnCluster<T>` in `pg-index-health/src/main/java/io/github/mfvanek/pg/health/checks/cluster/`.
5. **Register** — add a new entry to the `Diagnostic` enum and wire it into the Spring Boot starter.
6. **Document** — add Javadoc, update `doc/available_checks.md`.

## Code quality requirements

The build enforces:
- **100% instruction and branch coverage** (JaCoCo) — no exceptions; add tests rather than exclusions.
- **100% mutation score** (PiTest) — every conditional must be meaningfully tested.
- **Checkstyle** (`config/checkstyle/checkstyle.xml`) — 199-char line limit, Apache 2.0 license header required on all Java files.
- **PMD** (`config/pmd/pmd.xml`) and **SpotBugs** (`config/spotbugs/exclude.xml`).
- **ErrorProne + NullAway** at compile time — all types must carry `@Nullable` or `@NonNull` (JSpecify), or be in a `@NullMarked` class.

## Test conventions

- Use `containsExactly()` (AssertJ) to assert ordered results.
- Use `usingRecursiveFieldByFieldElementComparator()` for domain object comparison.
- Ignore size fields (`tableSizeInBytes`, `indexSizeInBytes`, `totalSize`) in comparisons, but assert they are `> 1L` separately.
- Test both ordinary tables/indexes and **partitioned** tables where applicable.
- Test with **quoted identifiers** (e.g., `"MyTable"`).
- Test fixtures and Testcontainers setup live in `pg-index-health-testing`.

## Null safety

All production classes must be `@NullMarked` (package-level or class-level) and use JSpecify annotations (`org.jspecify.annotations.Nullable` / `org.jspecify.annotations.NonNull`). NullAway enforces this at compile time. Test classes are excluded from NullAway.

## Key configuration files

| Path | Purpose |
|---|---|
| `gradle/libs.versions.toml` | All dependency versions |
| `buildSrc/src/main/kotlin/` | Convention plugins applied to each module |
| `config/checkstyle/checkstyle.xml` | Checkstyle rules |
| `config/pmd/pmd.xml` | PMD rules |
| `config/spotbugs/exclude.xml` | SpotBugs exclusions |
| `config/forbidden-apis/forbidden-apis.txt` | Forbidden API calls |
| `doc/available_checks.md` | Catalog of all checks with descriptions |
| `doc/custom_checks.md` | Guide for writing custom user-defined checks |
