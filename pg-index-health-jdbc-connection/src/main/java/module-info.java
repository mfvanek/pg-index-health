/**
 * Provides functionality for managing database connections in a PostgreSQL environment.
 * <p>
 * This module includes support for:
 * - Factories for creating connections.
 * - Handling connections to PostgreSQL hosts.
 * - Custom exception handling related to database connections.
 * <p>
 * Requires the following dependencies:
 * - jspecify: Enhances type-safety and nullability checks for annotated types.
 * - pg-model: Provides model definitions for PostgreSQL entities.
 * - java.sql: Interfaces and classes for working with SQL databases.
 * - commons-dbcp2: Provides the Apache Commons DBCP2 database connection pooling library.
 * <p>
 * Exports the following packages:
 * - io.github.mfvanek.pg.connection: Contains central classes for managing database connections.
 * - io.github.mfvanek.pg.connection.factory: Includes factory classes for creating and managing connections.
 * - io.github.mfvanek.pg.connection.host: Provides functionality for working with PostgreSQL hosts.
 * - io.github.mfvanek.pg.connection.exception: Contains custom exceptions specific to database connection errors.
 */
module io.github.mfvanek.pg.connection {
    requires org.jspecify;
    requires io.github.mfvanek.pg.model;
    requires java.sql;
    requires java.logging;
    requires org.apache.commons.dbcp2;

    exports io.github.mfvanek.pg.connection;
    exports io.github.mfvanek.pg.connection.factory;
    exports io.github.mfvanek.pg.connection.host;
    exports io.github.mfvanek.pg.connection.exception;
}
