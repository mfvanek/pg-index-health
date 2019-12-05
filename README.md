# pg-index-health
pg-index-health is a Java library for analyzing and maintaining indices health in Postgresql databases.

## Demo application
```java
import com.mfvanek.pg.connection.HighAvailabilityPgConnection;
import com.mfvanek.pg.connection.HighAvailabilityPgConnectionFactory;
import com.mfvanek.pg.connection.HighAvailabilityPgConnectionFactoryImpl;
import com.mfvanek.pg.connection.PgConnectionFactoryImpl;
import com.mfvanek.pg.index.health.logger.Exclusions;
import com.mfvanek.pg.index.health.logger.IndicesHealthLogger;
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
        final IndicesHealth indicesHealth = new IndicesHealthImpl(haPgConnection, new IndexMaintenanceFactoryImpl());
        final IndicesHealthLogger logger = new SimpleHealthLogger(indicesHealth, Exclusions.empty());
        logger.logAll().forEach(System.out::println);
    }
}
```
