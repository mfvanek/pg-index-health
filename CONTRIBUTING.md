## Building and testing

**Java 21** or above is required to build the project, but we still use Java 17 as a baseline.

1. Clone the repository
   ```shell
   git clone --recursive https://github.com/mfvanek/pg-index-health.git
   cd pg-index-health
   ```
2. Build with Gradle
   ```shell
   ./gradlew build
   ```

   This will build the project: run all tests (unit and [mutation](https://pitest.org/))
   and check [code coverage](https://www.jacoco.org/jacoco/trunk/index.html).  
   **You need to have [Docker](https://www.docker.com/) up and running**.
3. For fast verification you can skip mutation testing and `pitest` task
   ```shell
   ./gradlew test
   ```

By default, [PostgreSQL 18.1 from Testcontainers](https://www.testcontainers.org/) is used to run tests.  
Set `TEST_PG_VERSION` environment variable to use any of another available PostgreSQL version:
```
TEST_PG_VERSION=17.6-alpine
```
The list of all available PostgreSQL versions can be found [here](https://hub.docker.com/_/postgres).

## Implementing a new check

### Write a new SQL query (pg-index-health-sql repository)

Each database structure check starts with an SQL query to the [pg_catalog](https://www.postgresql.org/docs/current/catalogs.html).

1. SQL queries for checks are located in a separate repository https://github.com/mfvanek/pg-index-health-sql
2. That repository is pulled into the current project as a [git submodule](https://git-scm.com/book/en/v2/Git-Tools-Submodules)
3. [SQLFluff](https://github.com/sqlfluff/sqlfluff) is used as a linter for all SQL queries
4. All queries must be schema-aware (see [example](https://github.com/mfvanek/pg-index-health-sql/blob/6a5b823d2f86f3fed946f073de93a20245b8d312/sql/duplicated_indexes.sql#L23))

### Update git submodule

After your PR with a new SQL query is merged, you need to update the git submodule:

```shell
  git submodule foreach --recursive git pull origin master
```

### Extend the domain model (if needed)

`pg-index-health` is a [multimodule Gradle](https://docs.gradle.org/current/userguide/multi_project_builds.html) project.  
Domain model is located in a [pg-index-health-model](pg-index-health-model).

Best practices:
* All domain classes should be minimalistic and well-defined.
* All domain classes should have a [Jackson serializer and deserializer](jackson-integration/pg-index-health-model-jackson3-module).
* All domain classes should have a [builder](https://www.baeldung.com/java-builder-pattern).
* They should include enough information to generate corrective SQL migrations via [pg-index-health-generator](pg-index-health-generator).
* We use [JSpecify](https://github.com/jspecify/jspecify) to mark up the code with `Nullable`/`NonNull` annotations.

### Add the code for the new check

#### Execution on a specific host

* [Checks on a specific host](pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/host)

Implement a new class extending [AbstractCheckOnHost](pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/host/AbstractCheckOnHost.java).

#### Execution on the cluster

* [Checks on the cluster](pg-index-health/src/main/java/io/github/mfvanek/pg/health/checks/cluster)

Implement a new class extending [AbstractCheckOnCluster](pg-index-health/src/main/java/io/github/mfvanek/pg/health/checks/cluster/AbstractCheckOnCluster.java).

### Write proper tests

* Your code must be 100% covered.
* Use the `containsExactly()` method from **AssertJ** to validate the order in the check results.
* Use the `usingRecursiveFieldByFieldElementComparator()` method from **AssertJ** to validate database objects in the check results.
* Ignore fields with the size of tables and indexes in the check results
  via `usingRecursiveFieldByFieldElementComparatorIgnoringFields("totalSize", "indexes.indexSizeInBytes")`.  
  Make sure that the size is returned and non-zero:
  ```java
  .allMatch(i -> i.getIndexSizeInBytes() > 1L)
  .allMatch(i -> i.getBloatSizeInBytes() > 1L && i.getBloatPercentage() >= 14);
  ```
* Mutation tests via [pitest](https://pitest.org/) should work.
* Behavior of the new check should be tested for ordinary (non-partitioned) tables/indexes.
* Behavior of the new check should be tested for partitioned tables (if applicable).
* Behavior of the new check should be tested with [quoted identifiers](https://www.postgresql.org/docs/17/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS).

### Add documentation

1. Add Javadoc for all public classes and methods.
   Make sure that the Javadoc generation is working successfully:
   ```shell
   ./gradlew javadoc
   ```
2. Update [available_checks.md](doc/available_checks.md) and add information about the new check

### Register the new check

Register the new check in the standard check lists so it is included in the default set:

* [StandardChecksOnHost](pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/host/StandardChecksOnHost.java) — add a new instance to the `List.of(...)` in `apply(PgConnection)`.
* [StandardChecksOnCluster](pg-index-health/src/main/java/io/github/mfvanek/pg/health/checks/cluster/StandardChecksOnCluster.java) — add a new instance to the `List.of(...)` in `apply(HighAvailabilityPgConnection)`.

### Further steps

1. Update [Spring Boot starter](spring-boot-integration%2Fpg-index-health-test-starter).
2. Add sample code to the demo apps ([first](https://github.com/mfvanek/pg-index-health-demo/tree/master/pg-index-health-demo-without-spring), [second](https://github.com/mfvanek/pg-index-health-demo/tree/master/pg-index-health-spring-boot-demo)).
   Use a locally built pg-index-health version and send a draft PR.
