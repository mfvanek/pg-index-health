# pg-index-health
**pg-index-health** is a Java library for analyzing and maintaining indexes health in [Postgresql](https://www.postgresql.org/) databases.

## Supported PostgreSQL versions
* 9.6
* 10
* 11

## Available checks
**pg-index-health** allows you to detect the following problems:
1. Invalid (broken) indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/invalid_indexes.sql)).
1. Duplicated (completely identical) indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/duplicated_indexes.sql)).
1. Intersecting (partially identical) indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/intersecting_indexes.sql)).
1. Unused indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/unused_indexes.sql)).
1. Foreign keys without associated indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/foreign_keys_without_index.sql)).
1. Indexes with null values ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/indexes_with_null_values.sql)).
1. Tables with missing indexes ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/tables_with_missing_indexes.sql)).
1. Tables without primary key ([sql](https://github.com/mfvanek/pg-index-health/blob/master/src/main/resources/sql/tables_without_primary_key.sql)).

## Important note
**pg_index_health** uses the [PostgreSQL's statistics collector](https://www.postgresql.org/docs/10/monitoring-stats.html).  
You can call `pg_stat_reset()` to reset all statistics counters for the current database to zero.

## Demo application
```java
import com.mfvanek.pg.connection.HighAvailabilityPgConnection;
import com.mfvanek.pg.connection.HighAvailabilityPgConnectionFactory;
import com.mfvanek.pg.connection.HighAvailabilityPgConnectionFactoryImpl;
import com.mfvanek.pg.connection.PgConnectionFactoryImpl;
import com.mfvanek.pg.index.health.logger.Exclusions;
import com.mfvanek.pg.index.health.logger.IndexesHealthLogger;
import com.mfvanek.pg.index.health.logger.SimpleHealthLogger;
import com.mfvanek.pg.index.maintenance.MaintenanceFactoryImpl;
import com.mfvanek.pg.model.MemoryUnit;

public class DemoApp {

    public static void main(String[] args) {
        forTesting();
        forProduction();
    }

    private static void forTesting() {
        final String writeUrl = "jdbc:postgresql://host-name-1:6432,host-name-2:6432,host-name-3:6432/db_name_testing?targetServerType=master&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require";
        final String readUrl = "jdbc:postgresql://host-name-1:6432,host-name-2:6432,host-name-3:6432/db_name_testing?targetServerType=preferSlave&loadBalanceHosts=true&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require";
        final String userName = "user_name_testing";
        final String password = "password_testing";
        final HighAvailabilityPgConnectionFactory haPgConnectionFactory = new HighAvailabilityPgConnectionFactoryImpl(new PgConnectionFactoryImpl());
        final HighAvailabilityPgConnection haPgConnection = haPgConnectionFactory.of(writeUrl, userName, password, readUrl);
        final IndexesHealth indexesHealth = new IndexesHealthImpl(haPgConnection, new MaintenanceFactoryImpl());
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealth, Exclusions.empty());
        logger.logAll().forEach(System.out::println);
        // Resetting current statistics
        indexesHealth.resetStatistics();
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
        final var exclusions = Exclusions.builder().withIndexSizeThreshold(10, MemoryUnit.MB).build();
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealth, exclusions);
        logger.logAll().forEach(System.out::println);
    }
}
```
