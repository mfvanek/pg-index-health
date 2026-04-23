/**
 * Module definition for io.github.mfvanek.pg.generator.
 * This module provides tools for generating PostgreSQL-related functionalities.
 * It requires several external modules to operate, including:
 * - org.jspecify: for type annotations and null-safety.
 * - io.github.mfvanek.pg.model: for PostgreSQL model dependencies.
 * - java.logging: for logging capabilities.
 * <p>
 * The module exports the following packages:
 * - io.github.mfvanek.pg.generator: contains the core functionalities for the generator.
 * - io.github.mfvanek.pg.generator.utils: provides utility classes and methods to support the generator's operations.
 */
module io.github.mfvanek.pg.generator {
    requires org.jspecify;
    requires io.github.mfvanek.pg.model;
    requires java.logging;

    exports io.github.mfvanek.pg.generator;
    exports io.github.mfvanek.pg.generator.utils;
}
