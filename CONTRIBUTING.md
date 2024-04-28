## Building and testing

Java >= 11 is required.

1. Clone the repository

       git clone --recursive https://github.com/mfvanek/pg-index-health.git
       cd pg-index-health

2. Build with Gradle
    * On Linux and macOS: `./gradlew build`
    * On Windows: `.\gradlew.bat build`
    
   This will build the project and run tests.
    
By default, [PostgreSQL 16.2 from Testcontainers](https://www.testcontainers.org/) is used to run tests.  
Set `TEST_PG_VERSION` environment variable to use any of other available PostgreSQL version:
```
TEST_PG_VERSION=11.20-alpine
```
List of all available PostgreSQL versions can be found [here](https://hub.docker.com/_/postgres).

## Implementing a new check

### Write a new SQL query

Each database structure check starts with an SQL query to the pg_catalog.

1. SQL queries for checks are located in a separate repository https://github.com/mfvanek/pg-index-health-sql
2. That repository is pulled into the current project as a git submodule
3. [SQLFluff](https://github.com/sqlfluff/sqlfluff) is used as a linter for all sql queries
4. All requests must be schema-aware (see [example](https://github.com/mfvanek/pg-index-health-sql/blob/6a5b823d2f86f3fed946f073de93a20245b8d312/sql/duplicated_indexes.sql#L23))

### Extend domain model (if needed)

pg-index-health is a [multimodule Gradle](https://docs.gradle.org/current/userguide/multi_project_builds.html) project.  
Domain model is located in a [pg-index-health-model](https://github.com/mfvanek/pg-index-health/tree/master/pg-index-health-model).
All domain classes should be minimalistic and well-defined.
They should include enough information to generate corrective SQL migrations via [pg-index-health-generator](https://github.com/mfvanek/pg-index-health/tree/master/pg-index-health-generator).

### Add the code for the new check

All checks can be divided into 2 parts:
1. Runtime checks (those that make sense to run only on a production database)
2. Static checks (which can be run in tests on an empty database)

Runtime checks usually require aggregation of data from all nodes in the cluster.
Because of this, it became necessary to create [our own abstraction over the database connection](https://github.com/mfvanek/pg-index-health/tree/master/pg-index-health-jdbc-connection).

#### Execution on a specific host

* Checks on a specific host https://github.com/mfvanek/pg-index-health/tree/master/pg-index-health/src/main/java/io/github/mfvanek/pg/checks/host

Implement a new class extending [AbstractCheckOnHost](https://github.com/mfvanek/pg-index-health/blob/master/pg-index-health/src/main/java/io/github/mfvanek/pg/checks/host/AbstractCheckOnHost.java).

#### Execution on the cluster

* Checks on the cluster https://github.com/mfvanek/pg-index-health/tree/master/pg-index-health/src/main/java/io/github/mfvanek/pg/checks/cluster

Implement a new class extending [AbstractCheckOnCluster](https://github.com/mfvanek/pg-index-health/blob/master/pg-index-health/src/main/java/io/github/mfvanek/pg/checks/cluster/AbstractCheckOnCluster.java).

### Write proper tests

* Your code must be 100% covered.
* Mutation tests via [pitest](https://pitest.org/) should work.

### Further steps

1. Update readme and add information about the new check
2. Update [Spring Boot starter](https://github.com/mfvanek/pg-index-health-test-starter).
   Use a locally built pg-index-health version and send a draft PR.
3. Add sample code to the demo apps ([first](https://github.com/mfvanek/pg-index-health-demo), [second](https://github.com/mfvanek/pg-index-health-spring-boot-demo)).
   Use a locally built pg-index-health version and send a draft PR.
