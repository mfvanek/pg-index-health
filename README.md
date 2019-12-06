# pg-index-health
**pg-index-health** is a Java library for analyzing and maintaining indexes health in [Postgresql](https://www.postgresql.org/) databases.

## Supported PostgreSQL versions
* 10

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

## Demo application
```java
import com.mfvanek.pg.connection.HighAvailabilityPgConnection;
import com.mfvanek.pg.connection.HighAvailabilityPgConnectionFactory;
import com.mfvanek.pg.connection.HighAvailabilityPgConnectionFactoryImpl;
import com.mfvanek.pg.connection.PgConnectionFactoryImpl;
import com.mfvanek.pg.index.health.logger.Exclusions;
import com.mfvanek.pg.index.health.logger.IndexesHealthLogger;
import com.mfvanek.pg.index.health.logger.SimpleHealthLogger;
import com.mfvanek.pg.index.maintenance.IndexMaintenanceFactoryImpl;

public class DemoApp {

    public static void main(String[] args) {
        final String writeUrl = "jdbc:postgresql://host-name-1:6432,host-name-2:6432,host-name-3:6432/database_name?targetServerType=master&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require";
        final String readUrl = "jdbc:postgresql://host-name-1:6432,host-name-2:6432,host-name-3:6432/database_name?targetServerType=preferSlave&loadBalanceHosts=true&ssl=true&prepareThreshold=0&preparedStatementCacheQueries=0&sslmode=require";
        final String userName = "any_user_name";
        final String password = "any_password";
        final HighAvailabilityPgConnectionFactory haPgConnectionFactory = new HighAvailabilityPgConnectionFactoryImpl(new PgConnectionFactoryImpl());
        final HighAvailabilityPgConnection haPgConnection = haPgConnectionFactory.of(writeUrl, userName, password, readUrl);
        final IndexesHealth indexesHealth = new IndexesHealthImpl(haPgConnection, new IndexMaintenanceFactoryImpl());
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealth, Exclusions.empty());
        logger.logAll().forEach(System.out::println);
    }
}
```
