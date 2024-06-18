# ![pg-index-health](https://github.com/mfvanek/pg-index-health/blob/master/logo.png "pg-index-health")
**pg-index-health** is a Java library for analyzing and maintaining indexes and tables health in [PostgreSQL](https://www.postgresql.org/) databases.

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

## Supported PostgreSQL versions

[![PostgreSQL 12](https://img.shields.io/badge/PostgreSQL-12-green.svg)](https://www.postgresql.org/about/news/1976/ "PostgreSQL 12")
[![PostgreSQL 13](https://img.shields.io/badge/PostgreSQL-13-green.svg)](https://www.postgresql.org/about/news/postgresql-13-released-2077/ "PostgreSQL 13")
[![PostgreSQL 14](https://img.shields.io/badge/PostgreSQL-14-green.svg)](https://www.postgresql.org/about/news/postgresql-14-released-2318/ "PostgreSQL 14")
[![PostgreSQL 15](https://img.shields.io/badge/PostgreSQL-15-green.svg)](https://www.postgresql.org/about/news/postgresql-15-released-2526/ "PostgreSQL 15")
[![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16-green.svg)](https://www.postgresql.org/about/news/postgresql-16-released-2715/ "PostgreSQL 16")

### Support for previous versions of PostgreSQL

Compatibility with PostgreSQL versions **9.6**, **10** and **11** is no longer guaranteed, but it is very likely.  
We focus only on the currently maintained versions of PostgreSQL.  
For more information please see [PostgreSQL Versioning Policy](https://www.postgresql.org/support/versioning/).

### Supported Java versions

Supports [Java 11](https://www.java.com/en/) and above  
For **Java 8** compatible version take a look at release [0.7.0](https://github.com/mfvanek/pg-index-health/releases/tag/v.0.7.0) and lower

## Available checks

All checks can be divided into 2 groups:

1. Runtime checks (those that make sense to perform only on a production database with real data and statistics).  
   Runtime checks usually [require aggregating data from all nodes in the cluster](https://github.com/mfvanek/pg-index-health/blob/1af316152ed192d1e093491f709fe9bf4946e174/pg-index-health/src/main/java/io/github/mfvanek/pg/common/maintenance/Diagnostic.java#L111).
   This necessitated creating [our own abstraction over the database connection](https://github.com/mfvanek/pg-index-health/tree/master/pg-index-health-jdbc-connection).
2. Static checks (those can be run in tests on an empty database).  
   All static checks can be performed at runtime as well.

**pg-index-health** allows you to detect the following problems:

| №  | Description                                                                                                                        | Type               | SQL query                                                                                                           |
|----|------------------------------------------------------------------------------------------------------------------------------------|--------------------|---------------------------------------------------------------------------------------------------------------------|
| 1  | Invalid (broken) indexes                                                                                                           | **runtime**/static | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/invalid_indexes.sql)                           |
| 1  | Duplicated (completely identical) indexes                                                                                          | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/duplicated_indexes.sql)                        |
| 3  | Intersected (partially identical) indexes                                                                                          | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/intersected_indexes.sql)                       |
| 4  | Unused indexes                                                                                                                     | **runtime**        | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/unused_indexes.sql)                            |
| 5  | Foreign keys without associated indexes                                                                                            | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/foreign_keys_without_index.sql)                |
| 6  | Indexes with null values                                                                                                           | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_null_values.sql)                  |
| 7  | Tables with missing indexes                                                                                                        | **runtime**        | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_missing_indexes.sql)               |
| 8  | Tables without primary key                                                                                                         | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_without_primary_key.sql)                |
| 9  | Indexes [bloat](https://www.percona.com/blog/2018/08/06/basic-understanding-bloat-vacuum-postgresql-mvcc/)                         | **runtime**        | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/bloated_indexes.sql)                           |
| 10 | Tables [bloat](https://www.percona.com/blog/2018/08/06/basic-understanding-bloat-vacuum-postgresql-mvcc/)                          | **runtime**        | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/bloated_tables.sql)                            |
| 11 | Tables without [description](https://www.postgresql.org/docs/current/sql-comment.html)                                             | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_without_description.sql)                |
| 12 | Columns without [description](https://www.postgresql.org/docs/current/sql-comment.html)                                            | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_without_description.sql)               |
| 13 | Columns with [json](https://www.postgresql.org/docs/current/datatype-json.html) type                                               | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_json_type.sql)                    |
| 14 | Columns of [serial types](https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL) that are not primary keys | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/non_primary_key_columns_with_serial_types.sql) |
| 15 | Functions without [description](https://www.postgresql.org/docs/current/sql-comment.html)                                          | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/functions_without_description.sql)             |
| 16 | Indexes [with boolean](https://habr.com/ru/companies/tensor/articles/488104/)                                                      | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_boolean.sql)                      |
| 17 | Tables with [not valid constraints](https://habr.com/ru/articles/800121/)                                                          | **runtime**/static | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/check_not_valid_constraints.sql)               |
| 18 | B-tree indexes [on array columns](https://habr.com/ru/articles/800121/)                                                            | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/btree_indexes_on_array_columns.sql)            |
| 19 | [Sequence overflow](https://habr.com/ru/articles/800121/)                                                                          | **runtime**        | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/sequence_overflow.sql)                         |
| 20 | Primary keys with [serial types](https://wiki.postgresql.org/wiki/Don't_Do_This#Don.27t_use_serial)                                | static             | [sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/primary_keys_with_serial_types.sql)            |                                                                                                                     |

For raw sql queries see [pg-index-health-sql](https://github.com/mfvanek/pg-index-health-sql) project.

## How does it work?

### Static checks

Static checks are based on [information schema](https://www.postgresql.org/docs/current/information-schema.html)/[system catalogs](https://www.postgresql.org/docs/current/catalogs.html).

### Runtime checks

**pg_index_health** utilizes [the Cumulative Statistics System](https://www.postgresql.org/docs/current/monitoring-stats.html) 
(formerly known as [PostgreSQL's statistics collector](https://www.postgresql.org/docs/14/monitoring-stats.html)).

You can call `pg_stat_reset()` on each host to reset all statistics counters for the current database to zero
but the best way to do it is to use [DatabaseManagement::resetStatistics()](https://github.com/mfvanek/pg-index-health/blob/1af316152ed192d1e093491f709fe9bf4946e174/pg-index-health/src/main/java/io/github/mfvanek/pg/common/management/DatabaseManagement.java#L33) method.

## Installation

Using Gradle:
```groovy
implementation 'io.github.mfvanek:pg-index-health:0.12.0'
```

<details>
<summary>with Kotlin DSL</summary>

```kotlin
implementation("io.github.mfvanek:pg-index-health:0.12.0")
```
</details>

Using Maven:
```xml
<dependency>
  <groupId>io.github.mfvanek</groupId>
  <artifactId>pg-index-health</artifactId>
  <version>0.12.0</version>
</dependency>
```

## Articles and publications

* [Index health in PostgreSQL through the eyes of a Java developer](https://habr.com/ru/post/490824/)

## How to use

There are three main scenarios of using **pg-index-health** in your projects:
* unit\functional testing;
* collecting indexes health data and monitoring bloat;
* analysis of database configuration.

All these cases are covered with examples in the [pg-index-health-demo](https://github.com/mfvanek/pg-index-health-demo) project.

## Integration with Spring Boot

There is a Spring Boot starter [pg-index-health-test-starter](spring-boot-integration%2Fpg-index-health-test-starter)
for unit/integration testing as well.  
More examples you can find in [pg-index-health-spring-boot-demo](https://github.com/mfvanek/pg-index-health-spring-boot-demo) project.

### Starter installation

Using Gradle:

```groovy
testImplementation 'io.github.mfvanek:pg-index-health-test-starter:0.12.0'
```

<details>
<summary>with Kotlin DSL</summary>

```kotlin
testImplementation("io.github.mfvanek:pg-index-health-test-starter:0.12.0")
```

</details>

Using Maven:

```xml
<dependency>
    <groupId>io.github.mfvanek</groupId>
    <artifactId>pg-index-health-test-starter</artifactId>
    <version>0.12.0</version>
    <scope>test</scope>
</dependency>
```

### Spring Boot compatibility

| Spring Boot | Min JDK | pg-index-health-test-starter |
|-------------|---------|------------------------------|
| 2.4.x       | 8       | 0.3.x — 0.4.x                |
| 2.5.x       | 8       | 0.5.x — 0.6.x                |
| 2.6.x       | 8       | 0.7.x                        |
| 2.7.x       | 11      | 0.8.x — 0.12.x               |

#### Spring Boot 3 compatibility

* Starting from [0.9.0.1](https://github.com/mfvanek/pg-index-health-test-starter/releases/tag/v.0.9.0.1)
  added support for [Spring Boot 3.0](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide#auto-configuration-files)
* Starting from [0.10.2](https://github.com/mfvanek/pg-index-health-test-starter/releases/tag/v.0.10.2)
  added support for [Spring Boot 3.2](https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x#parameter-name-retention)

## Questions, issues, feature requests and contributions

* If you have any question or a problem with the library, please [file an issue](https://github.com/mfvanek/pg-index-health/issues).
* Contributions are always welcome! Please see [contributing guide](CONTRIBUTING.md) for more details.
* We utilize [Testcontainers](https://www.testcontainers.org/) for testing **pg-index-health**. 
So you need to have [Docker](https://www.docker.com/) installed on your machine.
