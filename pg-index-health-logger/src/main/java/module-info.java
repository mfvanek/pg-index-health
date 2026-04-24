/**
 * A module for logging health status and metrics of PostgreSQL instances using the pg-health framework.
 * <p>
 * This module provides logging capabilities for monitoring the state of PostgreSQL databases.
 * It integrates with several core components of the pg-health ecosystem, including model definitions,
 * database connection utilities, and health check modules. Logging is performed using the standard
 * Java Logging API.
 * <p>
 * Required dependencies:
 * - `org.jspecify` for nullness and type-checking annotations.
 * - `io.github.mfvanek.pg.model` for data models used within health checks.
 * - `io.github.mfvanek.pg.connection` for managing PostgreSQL connections.
 * - `io.github.mfvanek.pg.core` for core functionality required by pg-health.
 * - `io.github.mfvanek.pg.health` for health-checking utilities and interfaces.
 * - `java.logging` for handling logging operations.
 * - `java.sql` for database interactions.
 * <p>
 * Exports:
 * - `io.github.mfvanek.pg.health.logger` package, which contains classes and interfaces relevant
 *   to logging health metrics and statuses.
 */
module io.github.mfvanek.pg.health.logger {
    requires org.jspecify;
    requires io.github.mfvanek.pg.model;
    requires io.github.mfvanek.pg.connection;
    requires io.github.mfvanek.pg.core;
    requires io.github.mfvanek.pg.health;
    requires java.logging;
    requires java.sql;

    exports io.github.mfvanek.pg.health.logger;
}
