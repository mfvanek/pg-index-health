/**
 * Defines the module for PostgreSQL database core utilities and checks.
 * <p>
 * This module includes functionalities for performing various database
 * checks, managing statistics, and providing utility methods related
 * to PostgreSQL. It also facilitates connections to the database
 * and leverages logging for operational purposes. The following
 * packages are exported for use:
 * <p>
 * - io.github.mfvanek.pg.core.checks.common: Contains common database checks.
 * - io.github.mfvanek.pg.core.checks.host: Contains checks specific to database hosts.
 * - io.github.mfvanek.pg.core.checks.extractors: Provides mechanisms for extracting necessary data.
 * - io.github.mfvanek.pg.core.statistics: Manages and provides database statistical details.
 * - io.github.mfvanek.pg.core.utils: Contains utility methods for PostgreSQL operations.
 * - io.github.mfvanek.pg.core.utils.exception: Contains exception handling utilities.
 * <p>
 * The module requires the following modules:
 * - org.jspecify: For managing type annotations and nullness constraints.
 * - io.github.mfvanek.pg.model: For handling PostgreSQL-specific data models.
 * - io.github.mfvanek.pg.connection: Provides functionalities for managing database connections.
 * - java.logging: For logging purposes.
 * - java.sql: For handling database access and operations.
 */
module io.github.mfvanek.pg.core {
    requires org.jspecify;
    requires io.github.mfvanek.pg.model;
    requires io.github.mfvanek.pg.connection;
    requires java.logging;
    requires java.sql;

    exports io.github.mfvanek.pg.core.checks.common;
    exports io.github.mfvanek.pg.core.checks.host;
    exports io.github.mfvanek.pg.core.checks.extractors;
    exports io.github.mfvanek.pg.core.statistics;

    exports io.github.mfvanek.pg.core.utils;
    exports io.github.mfvanek.pg.core.utils.exception;
}
