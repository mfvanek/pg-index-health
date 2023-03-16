# ![pg-index-health](https://github.com/mfvanek/pg-index-health/blob/master/logo.png "pg-index-health")
**pg-index-health** is a Java library for analyzing and maintaining indexes health in [PostgreSQL](https://www.postgresql.org/) databases.

[![Java CI](https://github.com/mfvanek/pg-index-health/actions/workflows/tests.yml/badge.svg)](https://github.com/mfvanek/pg-index-health/actions/workflows/tests.yml "Java CI")
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mfvanek/pg-index-health.svg)](https://search.maven.org/artifact/io.github.mfvanek/pg-index-health/ "Maven Central")
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/mfvanek/pg-index-health/blob/master/LICENSE "Apache License 2.0")
[![javadoc](https://javadoc.io/badge2/io.github.mfvanek/pg-index-health/javadoc.svg)](https://javadoc.io/doc/io.github.mfvanek/pg-index-health "javadoc")
[![codecov](https://codecov.io/gh/mfvanek/pg-index-health/branch/master/graph/badge.svg)](https://codecov.io/gh/mfvanek/pg-index-health)

[![Total lines](https://tokei.rs/b1/github/mfvanek/pg-index-health)](https://github.com/mfvanek/pg-index-health)
[![Files](https://tokei.rs/b1/github/mfvanek/pg-index-health?category=files)](https://github.com/mfvanek/pg-index-health)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health&metric=bugs)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=mfvanek_pg-index-health&metric=coverage)](https://sonarcloud.io/summary/new_code?id=mfvanek_pg-index-health)

## Supported PostgreSQL versions
[![PostgreSQL 11](https://img.shields.io/badge/PostgreSQL-11-green.svg)](https://www.postgresql.org/about/news/1894/ "PostgreSQL 11")
[![PostgreSQL 12](https://img.shields.io/badge/PostgreSQL-12-green.svg)](https://www.postgresql.org/about/news/1976/ "PostgreSQL 12")
[![PostgreSQL 13](https://img.shields.io/badge/PostgreSQL-13-green.svg)](https://www.postgresql.org/about/news/postgresql-13-released-2077/ "PostgreSQL 13")
[![PostgreSQL 14](https://img.shields.io/badge/PostgreSQL-14-green.svg)](https://www.postgresql.org/about/news/postgresql-14-released-2318/ "PostgreSQL 14")
[![PostgreSQL 15](https://img.shields.io/badge/PostgreSQL-15-green.svg)](https://www.postgresql.org/about/news/postgresql-15-released-2526/ "PostgreSQL 15")

### Support for previous versions of PostgreSQL
Compatibility with PostgreSQL versions **9.6** and **10** is no longer guaranteed, but it is very likely.  
We focus only on the currently maintained versions of PostgreSQL.  
For more information please see [PostgreSQL Versioning Policy](https://www.postgresql.org/support/versioning/).

### Supported Java versions
Supports [Java 11](https://www.java.com/en/) and above  
For **Java 8** compatible version take a look at release [0.7.0](https://github.com/mfvanek/pg-index-health/releases/tag/v.0.7.0) and lower

## Available checks
**pg-index-health** allows you to detect the following problems:
1. Invalid (broken) indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/invalid_indexes.sql)).
2. Duplicated (completely identical) indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/duplicated_indexes.sql)).
3. Intersected (partially identical) indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/intersected_indexes.sql)).
4. Unused indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/unused_indexes.sql)).
5. Foreign keys without associated indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/foreign_keys_without_index.sql)).
6. Indexes with null values ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_null_values.sql)).
7. Tables with missing indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_missing_indexes.sql)).
8. Tables without primary key ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_without_primary_key.sql)).
9. Indexes [bloat](https://www.percona.com/blog/2018/08/06/basic-understanding-bloat-vacuum-postgresql-mvcc/) ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/bloated_indexes.sql)).
10. Tables [bloat](https://www.percona.com/blog/2018/08/06/basic-understanding-bloat-vacuum-postgresql-mvcc/) ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/bloated_tables.sql)).
11. Tables without [description](https://www.postgresql.org/docs/current/sql-comment.html) ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_without_description.sql)).
12. Columns without [description](https://www.postgresql.org/docs/current/sql-comment.html) ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_without_description.sql)).
13. Columns with [json](https://www.postgresql.org/docs/current/datatype-json.html) type ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_json_type.sql)).
14. Columns of [serial types](https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL) that are not primary keys ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/non_primary_key_columns_with_serial_types.sql)).
15. Functions without [description](https://www.postgresql.org/docs/current/sql-comment.html) ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/functions_without_description.sql)).

For raw sql queries see [pg-index-health-sql](https://github.com/mfvanek/pg-index-health-sql) project.

## How does it work?
**pg_index_health** utilizes [the Cumulative Statistics System](https://www.postgresql.org/docs/current/monitoring-stats.html) 
(formerly known as [PostgreSQL's statistics collector](https://www.postgresql.org/docs/14/monitoring-stats.html)).  
You can call `pg_stat_reset()` on each host to reset all statistics counters for the current database to zero
but the best way to do it is to use [DatabaseManagement::resetStatistics()](https://github.com/mfvanek/pg-index-health/blob/1b999374ebc4850ac60e70712399336175348f81/src/main/java/io/github/mfvanek/pg/common/management/DatabaseManagement.java#L32) method.

## Installation
Using Gradle:
```groovy
implementation 'io.github.mfvanek:pg-index-health:0.9.0'
```

Using Maven:
```xml
<dependency>
  <groupId>io.github.mfvanek</groupId>
  <artifactId>pg-index-health</artifactId>
  <version>0.9.0</version>
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
There is a Spring Boot starter [pg-index-health-test-starter](https://github.com/mfvanek/pg-index-health-test-starter) 
for unit/integration testing as well.  
More examples you can find in [pg-index-health-spring-boot-demo](https://github.com/mfvanek/pg-index-health-spring-boot-demo) project.

## Questions, issues, feature requests and contributions
* If you have any question or a problem with the library, please [file an issue](https://github.com/mfvanek/pg-index-health/issues).
* Contributions are always welcome! Please see [contributing guide](CONTRIBUTING.md) for more details.
* We utilize [Testcontainers](https://www.testcontainers.org/) for testing **pg-index-health**. 
So you need to have [Docker](https://www.docker.com/) installed on your machine.

## Related projects
[pg_analyse](https://github.com/idlesign/pg_analyse) - a set of tools to gather useful information from PostgreSQL,
written in Python, with command line interface.

## Acknowledgements
Supported by [JetBrains](https://www.jetbrains.com/) with [Licenses for Open Source Development](https://www.jetbrains.com/community/opensource/#support)
