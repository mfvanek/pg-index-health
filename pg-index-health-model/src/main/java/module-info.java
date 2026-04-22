/**
 * Module definition for the PostgreSQL utilities library.
 * This module provides models and utilities related to PostgreSQL objects and database management.
 * <p>
 * Requires:
 *   - org.jspecify: Indicates dependency on the JSpecify annotations library.
 * <p>
 * Exports:
 *   - io.github.mfvanek.pg.model.annotations: Contains annotations used in the library.
 *   - io.github.mfvanek.pg.model.bloat: Provides tools for detecting and managing database bloat.
 *   - io.github.mfvanek.pg.model.column: Contains representations of database column information.
 *   - io.github.mfvanek.pg.model.constraint: Provides models for database constraints.
 *   - io.github.mfvanek.pg.model.context: Contains context models used within the library.
 *   - io.github.mfvanek.pg.model.dbobject: Represents generic database objects.
 *   - io.github.mfvanek.pg.model.function: Models related to database functions.
 *   - io.github.mfvanek.pg.model.index: Represents database indexes and related functionality.
 *   - io.github.mfvanek.pg.model.predicates: Provides utilities that define common predicates for database objects.
 *   - io.github.mfvanek.pg.model.sequence: Contains models related to database sequences.
 *   - io.github.mfvanek.pg.model.settings: Provides representations for database settings and configurations.
 *   - io.github.mfvanek.pg.model.table: Represents database tables and their properties.
 *   - io.github.mfvanek.pg.model.units: Contains utility classes for working with data units.
 *   - io.github.mfvanek.pg.model.validation: Provides tools for validating database objects and configurations.
 */
module io.github.mfvanek.pg.model {
    requires org.jspecify;

    exports io.github.mfvanek.pg.model.annotations;
    exports io.github.mfvanek.pg.model.bloat;
    exports io.github.mfvanek.pg.model.column;
    exports io.github.mfvanek.pg.model.constraint;
    exports io.github.mfvanek.pg.model.context;
    exports io.github.mfvanek.pg.model.dbobject;
    exports io.github.mfvanek.pg.model.function;
    exports io.github.mfvanek.pg.model.index;
    exports io.github.mfvanek.pg.model.predicates;
    exports io.github.mfvanek.pg.model.sequence;
    exports io.github.mfvanek.pg.model.settings;
    exports io.github.mfvanek.pg.model.table;
    exports io.github.mfvanek.pg.model.units;
    exports io.github.mfvanek.pg.model.validation;
}
