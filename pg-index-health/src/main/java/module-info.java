/**
 * The {@code io.github.mfvanek.pg.health} module provides functionality for managing and monitoring the health
 * of PostgreSQL databases. It includes a collection of health checks and utility classes aimed at identifying and
 * addressing database issues.
 * <p>
 * Required Dependencies:
 * - org.jspecify: For type annotations and validation.
 * - io.github.mfvanek.pg.model: For database-related models.
 * - io.github.mfvanek.pg.connection: For handling database connections.
 * - io.github.mfvanek.pg.core: For core PostgreSQL utilities and processing.
 * - java.logging: For logging operations related to health checks.
 * - java.sql: For interacting with the database via SQL queries.
 * <p>
 * Exported Packages:
 * - {@code io.github.mfvanek.pg.health.checks.common}: Provides common health check implementations.
 * - {@code io.github.mfvanek.pg.health.checks.cluster}: Contains classes for performing health checks at a cluster level.
 * - {@code io.github.mfvanek.pg.health.checks.management}: Offers management-related health checks.
 * - {@code io.github.mfvanek.pg.health.utils}: Includes utility classes supporting health checks and related operations.
 */
module io.github.mfvanek.pg.health {
    requires org.jspecify;
    requires io.github.mfvanek.pg.model;
    requires io.github.mfvanek.pg.connection;
    requires io.github.mfvanek.pg.core;
    requires java.logging;
    requires java.sql;

    exports io.github.mfvanek.pg.health.checks.common;
    exports io.github.mfvanek.pg.health.checks.cluster;
    exports io.github.mfvanek.pg.health.checks.management;
    exports io.github.mfvanek.pg.health.utils;
}
