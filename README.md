# ![pg-index-health](https://github.com/mfvanek/pg-index-health/blob/master/logo.png "pg-index-health")
**pg-index-health** is a Java library for analyzing and maintaining indexes health in [PostgreSQL](https://www.postgresql.org/) databases.

[![Java CI](https://github.com/mfvanek/pg-index-health/workflows/Java%20CI/badge.svg)](https://github.com/mfvanek/pg-index-health/actions "Java CI")
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mfvanek/pg-index-health.svg)](https://search.maven.org/artifact/io.github.mfvanek/pg-index-health/ "Maven Central")
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/mfvanek/pg-index-health/blob/master/LICENSE "Apache License 2.0")
[![javadoc](https://javadoc.io/badge2/io.github.mfvanek/pg-index-health/javadoc.svg)](https://javadoc.io/doc/io.github.mfvanek/pg-index-health "javadoc")
[![codecov](https://codecov.io/gh/mfvanek/pg-index-health/branch/master/graph/badge.svg)](https://codecov.io/gh/mfvanek/pg-index-health)

## Supported PostgreSQL versions
[![PostgreSQL 9.6](https://img.shields.io/badge/PostgreSQL-9.6-green.svg)](https://www.postgresql.org/about/news/1703/ "PostgreSQL 9.6")
[![PostgreSQL 10](https://img.shields.io/badge/PostgreSQL-10-green.svg)](https://www.postgresql.org/about/news/1786/ "PostgreSQL 10")
[![PostgreSQL 11](https://img.shields.io/badge/PostgreSQL-11-green.svg)](https://www.postgresql.org/about/news/1894/ "PostgreSQL 11")
[![PostgreSQL 12](https://img.shields.io/badge/PostgreSQL-12-green.svg)](https://www.postgresql.org/about/news/1976/ "PostgreSQL 12")

## Available checks
**pg-index-health** allows you to detect the following problems:
1. Invalid (broken) indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/invalid_indexes.sql)).
1. Duplicated (completely identical) indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/duplicated_indexes.sql)).
1. Intersected (partially identical) indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/intersected_indexes.sql)).
1. Unused indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/unused_indexes.sql)).
1. Foreign keys without associated indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/foreign_keys_without_index.sql)).
1. Indexes with null values ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/indexes_with_null_values.sql)).
1. Tables with missing indexes ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_with_missing_indexes.sql)).
1. Tables without primary key ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/tables_without_primary_key.sql)).
1. Indexes bloat ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/bloated_indexes.sql)).
1. Tables bloat ([sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/bloated_tables.sql)).

For raw sql queries see [pg-index-health-sql](https://github.com/mfvanek/pg-index-health-sql) project.

## How does it work
**pg_index_health** utilizes the [PostgreSQL's statistics collector](https://www.postgresql.org/docs/10/monitoring-stats.html).  
You can call `pg_stat_reset()` on each host to reset all statistics counters for the current database to zero
but the best way to do it is to use [IndexesHealth::resetStatistics()](https://github.com/mfvanek/pg-index-health/blob/9251f99e2952bc7490137f40c83873ff54ac1ffa/src/main/java/io/github/mfvanek/pg/index/health/IndexesHealth.java#L168) method.

## Installation
Using Gradle:
```groovy
implementation 'io.github.mfvanek:pg-index-health:0.2.0'
```

Using Maven:
```xml
<dependency>
  <groupId>io.github.mfvanek</groupId>
  <artifactId>pg-index-health</artifactId>
  <version>0.2.0</version>
</dependency>
```

## How to use
There are three main scenarios of using **pg-index-health** in your projects:
* unit\functional testing;
* collecting indexes health data and monitoring bloat;
* analysis of database configuration.

All these cases are covered with examples in the [pg-index-health-demo](https://github.com/mfvanek/pg-index-health-demo) project.

## Questions, issues, feature requests and contributions
* If you have any question or a problem with the library, please [file an issue](https://github.com/mfvanek/pg-index-health/issues).
* Contributions are always welcome! Please see [contributing guide](CONTRIBUTING.md) for more details.

## Related projects
[pg_analyse](https://github.com/idlesign/pg_analyse) - a set of tools to gather useful information from PostgreSQL,
written in Python, with command line interface.
