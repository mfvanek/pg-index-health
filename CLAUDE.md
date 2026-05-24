# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this project is

**pg-index-health** is an embeddable Java library and schema linter for PostgreSQL.
It detects common anti-patterns in database schemas (missing indexes, bloat, bad column types, naming issues, etc.) by querying `pg_catalog`.
It ships as a multi-module Gradle project with Spring Boot integration, a CLI/demo layer, and support for both Jackson 2 and Jackson 3.

## Build and test commands

Docker must be running — tests use Testcontainers (PostgreSQL in Docker).

```bash
# Full build (unit tests + mutation tests + all quality checks)
./gradlew build

# Run only unit tests (without mutation tests)
./gradlew test

# Run tests in one module
./gradlew :pg-index-health-model:test

# Run a specific test class or method
./gradlew test --tests "*IndexesCheckOnHostTest"
./gradlew test --tests "*IndexesCheckOnHostTest.someMethod"

# Mutation tests
./gradlew pitest

# Verify Javadoc compiles
./gradlew javadoc

# Run individual quality checks without a full build
./gradlew checkstyleMain
./gradlew pmdMain
./gradlew spotbugsMain

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
| `pg-index-health-generator` | Generates corrective SQL migrations from check results. Extend when a new check type has a natural corrective SQL migration (e.g., `CREATE INDEX`, `ALTER TABLE`). |
| `pg-index-health-testing` | Testcontainers-based test fixtures shared across modules. |
| `pg-index-health-logger` | Logging utilities. |
| `pg-index-health-bom` | Bill of Materials for downstream dependency management. |
| `jackson-integration/pg-index-health-model-jackson2-module` | Jackson 2 serializers/deserializers (Spring Boot 3). |
| `jackson-integration/pg-index-health-model-jackson3-module` | Jackson 3 serializers/deserializers (Spring Boot 4). |
| `spring-boot-integration/pg-index-health-test-starter` | Spring Boot auto-configuration starter. |

SQL queries live in a **git submodule** (`pg-index-health-sql`) pointing to a separate repository. Clone with `--recursive`.

## Architecture

All database schema checks (also referred to as diagnostics) divided into two groups:
- Runtime checks (require statistics).
- Static checks (do not require statistics).

Runtime checks are meaningful only when executed on a live database instance in production.
These checks require accumulated statistics and aggregate this data from all hosts in the cluster.

Static checks do not require accumulated statistics and can be executed on the primary host immediately after applying migrations.

To obtain statistics for runtime checks we need to execute sql-queries on all hosts in the cluster.
For these purposes we have HighAvailabilityPgConnection.

All checks have two classes with -CheckOnHost and -CheckOnCluster endings.

## Implementing a new check (required steps)

1. **Add SQL** — write the query in the `pg-index-health-sql` submodule repo; merge it, then update the submodule here.
2. **Extend the domain model** (if needed) in `pg-index-health-model`. All domain classes need: a builder, JSpecify null annotations, and Jackson serializer/deserializer in **both** `jackson2` and `jackson3` modules.
3. **Test fixtures** — add an `Add*Statement.java` (and a partitioned variant if applicable) in `pg-index-health-testing/src/main/java/io/github/mfvanek/pg/testing/statements/`.
4. **Host-level check** — create a class named `*CheckOnHost` extending `AbstractCheckOnHost<T>` in `pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/host/`. Use `NamedParametersParser.parse()` for named SQL params, and one of the standard extractors in `pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/extractors/`.
5. **Cluster-level check** — create a class named `*CheckOnCluster` extending `AbstractCheckOnCluster<T>` in `pg-index-health/src/main/java/io/github/mfvanek/pg/health/checks/cluster/`.
6. **Register** — add a new entry to the `Diagnostic` enum in `pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/common/Diagnostic.java`, then add a `@Bean` method (with `@ConditionalOnMissingBean`) in the Spring Boot starter auto-configuration class. Also register the new check in `StandardChecksOnHost` (`pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/host/StandardChecksOnHost.java`) and `StandardChecksOnCluster` (`pg-index-health/src/main/java/io/github/mfvanek/pg/health/checks/cluster/StandardChecksOnCluster.java`).
7. **Per-check doc** — create `doc/eng/<check_name>.md` with a reproduction SQL script and recommended fix. Use an existing file (e.g., `doc/eng/foreign_keys_with_null_values.md`) as a template.
8. **Document** — add Javadoc to new classes, update the table in `doc/available_checks.md` (columns: №, Description, Type, Supports partitioning, SQL query link).

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
- Name host-level test classes `*CheckOnHostTest` and cluster-level test classes `*CheckOnClusterTest`.

## Common pitfalls

- **Both Jackson modules** — adding a serializer/deserializer to `jackson2` but forgetting `jackson3` (or vice versa) compiles fine but breaks one Spring Boot generation.
- **`@ConditionalOnMissingBean` required** — every `@Bean` in the Spring Boot auto-configuration must carry this annotation; omitting it silently overrides user-provided beans.
- **Partitioned table tests** — if a check is relevant to partitioned tables, test it explicitly; JaCoCo enforces 100% branch coverage so a missing path fails the build.
- **Diagnostic enum + starter** — forgetting either the `Diagnostic` entry or the Spring Boot `@Bean` leaves the check invisible to the auto-configured integration.

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

## Git workflow

**Never create a git commit unless the user explicitly asks.** Make all file edits freely, but wait for an explicit instruction (e.g. "commit", "commit the changes") before running any `git commit` command.
