/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A mapping to commonly used PostgreSQL column types.
 * <p>
 * Type names are exactly the same as the SQL queries return them ({@code atttypid::regtype::text} without typmod).
 * Serial pseudo-types are used by checks for serial columns.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Data Types</a>
 * @since 0.60.0
 */
public enum PgColumnType {

    /**
     * Constant for the PostgreSQL "smallint" type.
     */
    SMALLINT("smallint"),
    /**
     * Constant for the PostgreSQL "integer" type.
     */
    INTEGER("integer"),
    /**
     * Constant for the PostgreSQL "bigint" type.
     */
    BIGINT("bigint"),
    /**
     * Constant for the PostgreSQL "numeric" type.
     */
    NUMERIC("numeric"),
    /**
     * Constant for the PostgreSQL "boolean" type.
     */
    BOOLEAN("boolean"),
    /**
     * Constant for the PostgreSQL "character" type.
     */
    CHARACTER("character"),
    /**
     * Constant for the PostgreSQL "character varying" type.
     */
    VARCHAR("character varying"),
    /**
     * Constant for the PostgreSQL "text" type.
     */
    TEXT("text"),
    /**
     * Constant for the PostgreSQL "uuid" type.
     */
    UUID("uuid"),
    /**
     * Constant for the PostgreSQL "date" type.
     */
    DATE("date"),
    /**
     * Constant for the PostgreSQL "timestamp without time zone" type.
     */
    TIMESTAMP("timestamp without time zone"),
    /**
     * Constant for the PostgreSQL "timestamp with time zone" type.
     */
    TIMESTAMPTZ("timestamp with time zone"),
    /**
     * Constant for the PostgreSQL "time with time zone" type.
     */
    TIMETZ("time with time zone"),
    /**
     * Constant for the PostgreSQL "json" type.
     */
    JSON("json"),
    /**
     * Constant for the PostgreSQL "jsonb" type.
     */
    JSONB("jsonb"),
    /**
     * Constant for the PostgreSQL "bytea" type.
     */
    BYTEA("bytea"),
    /**
     * Constant for the PostgreSQL "oid" type.
     */
    OID("oid"),
    /**
     * Constant for the PostgreSQL "money" type.
     */
    MONEY("money"),
    /**
     * Constant for the PostgreSQL "smallserial" pseudo-type.
     *
     * @see <a href="https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL">Serial types</a>
     */
    SMALL_SERIAL("smallserial"),
    /**
     * Constant for the PostgreSQL "serial" pseudo-type.
     *
     * @see <a href="https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL">Serial types</a>
     */
    SERIAL("serial"),
    /**
     * Constant for the PostgreSQL "bigserial" pseudo-type.
     *
     * @see <a href="https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL">Serial types</a>
     */
    BIG_SERIAL("bigserial");

    private static final Map<String, PgColumnType> VALUES = new HashMap<>();

    static {
        for (final PgColumnType pgColumnType : values()) {
            VALUES.put(pgColumnType.columnType, pgColumnType);
        }
    }

    private final String columnType;

    PgColumnType(final String columnType) {
        this.columnType = Objects.requireNonNull(columnType);
    }

    /**
     * Gets the PostgreSQL name of the column type.
     *
     * @return the PostgreSQL name of the column type
     */
    public String getColumnType() {
        return columnType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return PgColumnType.class.getSimpleName() + '{' +
            "columnType='" + columnType + '\'' +
            '}';
    }

    /**
     * Gets {@code PgColumnType} from the PostgreSQL name of the column type.
     *
     * @param pgColumnType the PostgreSQL name of the column type; should be non-null.
     * @return {@code PgColumnType}
     * @throws IllegalArgumentException if the given type is not known
     */
    public static PgColumnType valueFrom(final String pgColumnType) {
        Objects.requireNonNull(pgColumnType, "pgColumnType cannot be null");
        final PgColumnType result = VALUES.get(pgColumnType);
        if (result == null) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "pgColumnType = '%s'", pgColumnType));
        }
        return result;
    }
}
