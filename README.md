# ![pg-index-health](https://github.com/mfvanek/pg-index-health/blob/master/logo.png "pg-index-health")

**pg-index-health** is an embeddable schema linter for PostgreSQL that detects common anti-patterns and promotes best practices.

[![Java CI](https://github.com/mfvanek/pg-index-health/actions/workflows/tests.yml/badge.svg)](https://github.com/mfvanek/pg-index-health/actions/workflows/tests.yml "Java CI")
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mfvanek/pg-index-health.svg)](https://search.maven.org/artifact/io.github.mfvanek/pg-index-health/ "Maven Central")
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/mfvanek/pg-index-health/blob/master/LICENSE "Apache License 2.0")
[![javadoc](https://javadoc.io/badge2/io.github.mfvanek/pg-index-health/javadoc.svg)](https://javadoc.io/doc/io.github.mfvanek/pg-index-health "javadoc")
[![codecov](https://codecov.io/gh/mfvanek/pg-index-health/branch/master/graph/badge.svg)](https://codecov.io/gh/mfvanek/pg-index-health)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health&metric=bugs)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health&metric=coverage)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health)

[![Mutation testing badge](https://img.shields.io/endpoint?style=flat&url=https%3A%2F%2Fbadge-api.stryker-mutator.io%2Fgithub.com%2Fmfvanek%2Fpg-index-health%2Fmaster)](https://dashboard.stryker-mutator.io/reports/github.com/mfvanek/pg-index-health/master)

## What is this?

**pg-index-health** is a Java library designed to analyze PostgreSQL database schemas and help developers build efficient, reliable applications.

It scans database structures to identify common schema-level pitfalls that often go unnoticed until they cause problems in production.

By embedding **pg-index-health** into your CI/CD pipeline, you can proactively catch schema design flaws, enforce consistency, and adhere to PostgreSQL best practices before they impact performance or reliability.

## Available checks

A list of all available checks (rules/diagnostics) can be found [here](doc/available_checks.md).  
In code, you can get all checks through an instance of the [StandardChecksOnHost](pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/host/StandardChecksOnHost.java) or [StandardChecksOnCluster](pg-index-health/src/main/java/io/github/mfvanek/pg/health/checks/cluster/StandardChecksOnCluster.java) class.

## Installation

Using Gradle:

```kotlin
implementation("io.github.mfvanek:pg-index-health:0.20.3")
```

Using Maven:

```xml
<dependency>
  <groupId>io.github.mfvanek</groupId>
  <artifactId>pg-index-health</artifactId>
  <version>0.20.3</version>
</dependency>
```

## How to use

There are two main scenarios of using **pg-index-health** in your projects:
* unit\functional testing (see **standard test** in a section below) locally and in CI for compliance of the database structure with best practices;
* collecting indexes, tables and sequences health data and bloat monitoring in runtime on production.

All these cases are covered with examples in the [pg-index-health-demo](https://github.com/mfvanek/pg-index-health-demo) project.

## Integration with Spring Boot

There is a Spring Boot starter [pg-index-health-test-starter](spring-boot-integration%2Fpg-index-health-test-starter)
for unit/integration testing as well.  
More examples you can find in [pg-index-health-demo](https://github.com/mfvanek/pg-index-health-demo) project.

### Starter installation

Using Gradle:

```kotlin
testImplementation("io.github.mfvanek:pg-index-health-test-starter:0.20.3")
```

Using Maven:

```xml
<dependency>
    <groupId>io.github.mfvanek</groupId>
    <artifactId>pg-index-health-test-starter</artifactId>
    <version>0.20.3</version>
    <scope>test</scope>
</dependency>
```

### Standard test

Add a standard test to your project as shown below. Ideally, all checks should pass and return an empty result.

```java
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.predicates.SkipByColumnNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipFlywayTablesPredicate;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipLiquibaseTablesPredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseStructureStaticAnalysisTest {

    @Autowired
    private List<DatabaseCheckOnHost<? extends @NonNull DbObject>> checks;

    @Test
    void checksShouldWork() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());

        // Predicates allow you to customize the list of permanent exceptions for your project.
        // Just filter out the database objects that you definitely don't want to see in the check results.
        // Do not use predicates for temporary exceptions or recording deviations in the database structure.
        final Predicate<DbObject> exclusions = SkipLiquibaseTablesPredicate.ofDefault()
            .and(SkipFlywayTablesPredicate.ofDefault()) // if you use Flyway
            .and(SkipTablesByNamePredicate.ofName("my_awesome_table"))
            .and(SkipIndexesByNamePredicate.ofName("my_awesome_index"))
            .and(SkipByColumnNamePredicate.ofName("id"));

        checks.stream()
            .filter(DatabaseCheckOnHost::isStatic)
            .forEach(c ->
                assertThat(c.check(exclusions))
                    .as(c.getName())
                    .isEmpty());
    }
}

```

<details>
<summary>with Kotlin</summary>

```kotlin
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost
import io.github.mfvanek.pg.core.checks.common.Diagnostic
import io.github.mfvanek.pg.model.dbobject.DbObject
import io.github.mfvanek.pg.model.predicates.SkipByColumnNamePredicate
import io.github.mfvanek.pg.model.predicates.SkipFlywayTablesPredicate
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate
import io.github.mfvanek.pg.model.predicates.SkipLiquibaseTablesPredicate
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class DatabaseStructureStaticAnalysisTest {

    @Autowired
    private lateinit var checks: List<DatabaseCheckOnHost<out DbObject>>

    @Test
    fun checksShouldWork() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.entries.toTypedArray())

        // Predicates allow you to customize the list of permanent exceptions for your project.
        // Just filter out the database objects that you definitely don't want to see in the check results.
        // Do not use predicates for temporary exceptions or recording deviations in the database structure.
        val exclusions = SkipLiquibaseTablesPredicate.ofDefault()
            .and(SkipFlywayTablesPredicate.ofDefault()) // if you use Flyway
            .and(SkipTablesByNamePredicate.ofName("my_awesome_table"))
            .and(SkipIndexesByNamePredicate.ofName("my_awesome_index"))
            .and(SkipByColumnNamePredicate.ofName("id"))

        checks
            .filter { it.isStatic }
            .forEach {
                assertThat(it.check(exclusions))
                    .`as`(it.name)
                    .isEmpty()
            }
    }
}
```

</details>

#### Recording deviations in the database structure

All deviations in the database structure found during checks can be recorded in the code and fixed later.

<details>
<summary>with Java</summary>

```java
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.table.Table;
import org.assertj.core.api.ListAssert;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseStructureStaticAnalysisTest {

    @Autowired
    private List<DatabaseCheckOnHost<? extends @NonNull DbObject>> checks;

    @Test
    void checksShouldWorkForAdditionalSchema() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());

        final PgContext ctx = PgContext.of("additional_schema");
        checks.stream()
            .filter(DatabaseCheckOnHost::isStatic)
            .forEach(c -> {
                final ListAssert<? extends DbObject> listAssert = assertThat(c.check(ctx))
                    .as(c.getName());

                switch (c.getName()) {
                    case "TABLES_WITHOUT_DESCRIPTION", "TABLES_NOT_LINKED_TO_OTHERS" -> listAssert
                        .hasSize(1)
                        .asInstanceOf(list(Table.class))
                        .containsExactly(Table.of(ctx, "additional_table"));

                    case "COLUMNS_WITHOUT_DESCRIPTION" -> listAssert
                        .hasSize(2)
                        .asInstanceOf(list(Column.class))
                        .containsExactly(
                            Column.ofNotNull(ctx, "additional_table", "id"),
                            Column.ofNotNull(ctx, "additional_table", "name")
                        );

                    case "PRIMARY_KEYS_WITH_SERIAL_TYPES" -> listAssert
                        .hasSize(1)
                        .asInstanceOf(list(ColumnWithSerialType.class))
                        .containsExactly(
                            ColumnWithSerialType.ofBigSerial(ctx, Column.ofNotNull(ctx, "additional_table", "id"), "additional_table_id_seq")
                        );

                    default -> listAssert.isEmpty();
                }
            });
    }
}
```

</details>

### Spring Boot compatibility

| Spring Boot | Min JDK | pg-index-health-test-starter |
|-------------|---------|------------------------------|
| 2.4.x       | 8       | 0.3.x — 0.4.x                |
| 2.5.x       | 8       | 0.5.x — 0.6.x                |
| 2.6.x       | 8       | 0.7.x                        |
| 2.7.x       | 11      | 0.8.x — 0.15.x               |
| 3.3.x       | 17      | 0.20.x                       |
| 3.4.x       | 17      | 0.30.x                       |

#### Spring Boot 3 compatibility

* Starting from [0.9.0.1](https://github.com/mfvanek/pg-index-health-test-starter/releases/tag/v.0.9.0.1)
  added support for [Spring Boot 3.0](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide#auto-configuration-files)
* Starting from [0.10.2](https://github.com/mfvanek/pg-index-health-test-starter/releases/tag/v.0.10.2)
  added support for [Spring Boot 3.2](https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x#parameter-name-retention)

## Supported PostgreSQL versions

[![PostgreSQL 13](https://img.shields.io/badge/PostgreSQL-13-green.svg)](https://www.postgresql.org/about/news/postgresql-13-released-2077/ "PostgreSQL 13")
[![PostgreSQL 14](https://img.shields.io/badge/PostgreSQL-14-green.svg)](https://www.postgresql.org/about/news/postgresql-14-released-2318/ "PostgreSQL 14")
[![PostgreSQL 15](https://img.shields.io/badge/PostgreSQL-15-green.svg)](https://www.postgresql.org/about/news/postgresql-15-released-2526/ "PostgreSQL 15")
[![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-green.svg)](https://www.postgresql.org/about/news/postgresql-16-released-2715/ "PostgreSQL 16")
[![PostgreSQL 17](https://img.shields.io/badge/PostgreSQL-17-green.svg)](https://www.postgresql.org/about/news/postgresql-17-released-2936/ "PostgreSQL 17")

### Support for previous versions of PostgreSQL

Compatibility with PostgreSQL versions **10**, **11** and **12** is no longer guaranteed, but it is very likely.  
We focus only on the currently maintained versions of PostgreSQL.  
For more information please see [PostgreSQL Versioning Policy](https://www.postgresql.org/support/versioning/).

## Supported Java versions

Supports [Java 17](https://www.java.com/en/) and above.

- For **Java 11** compatible version take a look at release [0.15.0](https://github.com/mfvanek/pg-index-health/releases/tag/v.0.15.0) and lower.
- For **Java 8** compatible version take a look at release [0.7.0](https://github.com/mfvanek/pg-index-health/releases/tag/v.0.7.0) and lower.

## Articles and publications

### In English

* [pg-index-health – a static analysis tool for you PostgreSQL database](https://dev.to/mfvanek/pg-index-health-a-static-analysis-tool-for-you-postgresql-database-2no5)

### In Russian

* [pg-index-health – static analysis of the structure of PostgreSQL databases](https://habr.com/ru/articles/871546/)
* [Index health in PostgreSQL through the eyes of a Java developer](https://habr.com/ru/post/490824/)
* [DBA: finding useless indexes](https://habr.com/ru/companies/tensor/articles/488104/)
* [The series of articles "Static analysis of the database structure"](https://habr.com/ru/articles/800121/)

## Questions, issues, feature requests and contributions

* If you have any question or a problem with the library, please [file an issue](https://github.com/mfvanek/pg-index-health/issues).
* Contributions are always welcome! Please see [contributing guide](CONTRIBUTING.md) for more details.
* We utilize [Testcontainers](https://www.testcontainers.org/) for testing **pg-index-health**. 
So you need to have [Docker](https://www.docker.com/) installed on your machine.

## Similar solutions

- [PostgreSQL Wiki - Don't Do This](https://wiki.postgresql.org/wiki/Don%27t_Do_This) — a short list of common mistakes.
- [db_verifier](https://github.com/sdblist/db_verifier) — scripts to check the database structure for errors or non-recommended practices.
- [SchemaCrawler Linter](https://www.schemacrawler.com/lint.html) — a free database schema linter.
- [Squawk](https://github.com/sbdchd/squawk) — a linter for Postgres migrations.
- [schemalint](https://github.com/kristiandupont/schemalint) — a Postgres databases schema linter.
- [index-digest](https://github.com/macbre/index-digest) — a database linter for MySQL and MariaDB.
- [Azimutt](https://azimutt.app/features/analysis) — a linter for database.
