/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.dbobject;

import io.github.mfvanek.pg.model.constraint.ConstraintType;

import java.util.Objects;

/**
 * A mapping to PostgreSQL object types.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/current/catalog-pg-class.html">relkind</a>
 * @since 0.13.2
 */
public enum PgObjectType {
    /**
     * A table (relation, entity) in a database.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-createtable.html">CREATE TABLE</a>
     */
    TABLE("table"),
    /**
     * A partitioned table in a database.
     *
     * @see <a href="https://www.postgresql.org/docs/current/ddl-partitioning.html">Table Partitioning</a>
     * @since 0.14.4
     */
    PARTITIONED_TABLE("partitioned table"),
    /**
     * An index in a database.
     *
     * @see <a href="https://www.postgresql.org/docs/current/indexes-types.html">Index Types</a>
     */
    INDEX("index"),
    /**
     * A partitioned index in a database.
     *
     * @see <a href="https://www.postgresql.org/docs/current/ddl-partitioning.html">Table Partitioning</a>
     * @since 0.14.4
     */
    PARTITIONED_INDEX("partitioned index"),
    /**
     * A sequence in a database.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-createsequence.html">CREATE SEQUENCE</a>
     */
    SEQUENCE("sequence"),
    /**
     * A view in a database.
     * It's a named query that you can refer to like an ordinary table.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-createview.html">CREATE VIEW</a>
     */
    VIEW("view"),
    /**
     * A materialized view of a query.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-creatematerializedview.html">CREATE MATERIALIZED VIEW</a>
     */
    MATERIALIZED_VIEW("materialized view"),
    /**
     * A function in a database.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-createfunction.html">CREATE FUNCTION</a>
     */
    FUNCTION("function"),
    /**
     * A constraint in a database.
     *
     * @see <a href="https://www.postgresql.org/docs/current/ddl-constraints.html">Constraints</a>
     * @see ConstraintType
     */
    CONSTRAINT("constraint");

    private final String objectType;

    PgObjectType(final String objectType) {
        this.objectType = Objects.requireNonNull(objectType, "objectType");
    }

    /**
     * Retrieves {@code PgObjectType} from given literal representation.
     *
     * @param objectType literal PostgreSQL object type; should be non-null.
     * @return {@code PgObjectType}
     */
    public static PgObjectType valueFrom(final String objectType) {
        Objects.requireNonNull(objectType, "objectType cannot be null");
        for (final PgObjectType pgObjectType : values()) {
            if (pgObjectType.objectType.equalsIgnoreCase(objectType)) {
                return pgObjectType;
            }
        }
        throw new IllegalArgumentException("Unknown objectType: " + objectType);
    }
}
