# <img src="logo.png" width="100%">
**pg-index-health** is a Java library for analyzing and maintaining indexes health in [PostgreSQL](https://www.postgresql.org/) databases.

![Java CI](https://github.com/mfvanek/pg-index-health/workflows/Java%20CI/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mfvanek/pg-index-health.svg)](https://search.maven.org/artifact/io.github.mfvanek/pg-index-health/)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://github.com/mfvanek/pg-index-health/blob/master/LICENSE)

## Supported PostgreSQL versions
* 9.6
* 10
* 11
* 12

## Available checks
**pg-index-health** allows you to detect the following problems:
1. Invalid (broken) indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/invalid_indexes.sql)).
1. Duplicated (completely identical) indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/duplicated_indexes.sql)).
1. Intersected (partially identical) indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/intersected_indexes.sql)).
1. Unused indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/unused_indexes.sql)).
1. Foreign keys without associated indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/foreign_keys_without_index.sql)).
1. Indexes with null values ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/indexes_with_null_values.sql)).
1. Tables with missing indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/tables_with_missing_indexes.sql)).
1. Tables without primary key ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/tables_without_primary_key.sql)).
1. Indexes bloat ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/bloated_indexes.sql)).
1. Tables bloat ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/bloated_tables.sql)).

## Important note
**pg_index_health** uses the [PostgreSQL's statistics collector](https://www.postgresql.org/docs/10/monitoring-stats.html).  
You can call `pg_stat_reset()` to reset all statistics counters for the current database to zero.

## Installation

Using Gradle:
```groovy
implementation 'io.github.mfvanek:pg-index-health:0.1.7'
```

Using Maven:
```xml
<dependency>
  <groupId>io.github.mfvanek</groupId>
  <artifactId>pg-index-health</artifactId>
  <version>0.1.7</version>
</dependency>
```

## Demo application
```java
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactory;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactoryImpl;
import io.github.mfvanek.pg.connection.PgConnectionFactoryImpl;
import io.github.mfvanek.pg.index.health.IndexesHealth;
import io.github.mfvanek.pg.index.health.IndexesHealthImpl;
import io.github.mfvanek.pg.index.health.logger.Exclusions;
import io.github.mfvanek.pg.index.health.logger.IndexesHealthLogger;
import io.github.mfvanek.pg.index.health.logger.SimpleHealthLogger;
import io.github.mfvanek.pg.index.maintenance.MaintenanceFactoryImpl;
import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.model.PgContext;

public class DemoApp {

    public static void main(String[] args) {
        loadDriver();
        forTesting();
        forProduction();
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void forTesting() {
        final String writeUrl = "jdbc:postgresql://host-name-1:6432,host-name-2:6432,host-name-3:6432/db_name_testing?targetServerType=master&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require";
        final String readUrl = "jdbc:postgresql://host-name-1:6432,host-name-2:6432,host-name-3:6432/db_name_testing?targetServerType=preferSlave&loadBalanceHosts=true&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require";
        final String userName = "user_name_testing";
        final String password = "password_testing";
        final HighAvailabilityPgConnectionFactory haPgConnectionFactory = new HighAvailabilityPgConnectionFactoryImpl(new PgConnectionFactoryImpl());
        final HighAvailabilityPgConnection haPgConnection = haPgConnectionFactory.of(writeUrl, userName, password, readUrl);
        final IndexesHealth indexesHealth = new IndexesHealthImpl(haPgConnection, new MaintenanceFactoryImpl());
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealth);
        logger.logAll(Exclusions.empty(), PgContext.ofPublic())
                .forEach(System.out::println);
        // Resetting current statistics
        // indexesHealth.resetStatistics();
    }

    private static void forProduction() {
        final String writeUrl = "jdbc:postgresql://host-name-1:6432,host-name-2:6432,host-name-3:6432/db_name_production?ssl=true&targetServerType=master&prepareThreshold=0&preparedStatementCacheQueries=0&connectTimeout=2&socketTimeout=50&loginTimeout=10&sslmode=require";
        final String readUrl = "jdbc:postgresql://host-name-1:6432,host-name-2:6432,host-name-3:6432,host-name-4:6432,host-name-5:6432/db_name_production?ssl=true&targetServerType=preferSlave&loadBalanceHosts=true&prepareThreshold=0&preparedStatementCacheQueries=0&connectTimeout=2&socketTimeout=50&loginTimeout=10&sslmode=require";
        final String cascadeAsyncReadUrl = "jdbc:postgresql://host-name-6:6432/db_name_production?ssl=true&targetServerType=preferSlave&loadBalanceHosts=true&prepareThreshold=0&preparedStatementCacheQueries=0&connectTimeout=2&socketTimeout=50&loginTimeout=10&sslmode=require";
        final String userName = "user_name_production";
        final String password = "password_production";
        final HighAvailabilityPgConnectionFactory haPgConnectionFactory = new HighAvailabilityPgConnectionFactoryImpl(new PgConnectionFactoryImpl());
        final HighAvailabilityPgConnection haPgConnection = haPgConnectionFactory.of(writeUrl, userName, password, readUrl, cascadeAsyncReadUrl);
        final IndexesHealth indexesHealth = new IndexesHealthImpl(haPgConnection, new MaintenanceFactoryImpl());
        final Exclusions exclusions = Exclusions.builder()
                .withIndexSizeThreshold(10, MemoryUnit.MB)
                .withTableSizeThreshold(10, MemoryUnit.MB)
                .build();
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealth);
        logger.logAll(exclusions, PgContext.ofPublic())
                .forEach(System.out::println);
    }
}
```

For more examples see [pg-index-health-demo](https://github.com/mfvanek/pg-index-health-demo) project.

## Questions, issues, feature requests and contributions

 * If you have any question or a problem with the library, please [file an issue](https://github.com/mfvanek/pg-index-health/issues).
 * Contributions are always welcome! Please see [contributing guide](CONTRIBUTING.md) for more details.
