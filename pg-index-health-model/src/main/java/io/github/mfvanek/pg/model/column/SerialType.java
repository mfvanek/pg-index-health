/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * A mapping to PostgreSQL serial types.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL">Serial types</a>
 * @since 0.6.2
 */
public enum SerialType {

    /**
     * Constant for the PostgreSQL "smallserial" type.
     */
    SMALL_SERIAL("smallserial"),
    /**
     * Constant for the PostgreSQL "serial" type.
     */
    SERIAL("serial"),
    /**
     * Constant for the PostgreSQL "bigserial" type.
     */
    BIG_SERIAL("bigserial");

    private static final Map<String, SerialType> VALUES = new HashMap<>();

    static {
        for (final SerialType serialType : values()) {
            VALUES.put(serialType.columnType, serialType);
        }
    }

    private final String columnType;

    SerialType(@Nonnull final String columnType) {
        this.columnType = Objects.requireNonNull(columnType);
    }

    /**
     * Returns the string representation of this PostgreSQL serial type.
     *
     * @return the column type as a string, never {@code null}
     */
    @Nonnull
    public String getColumnType() {
        return columnType;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return SerialType.class.getSimpleName() + '{' +
            "columnType='" + columnType + '\'' +
            '}';
    }

    /**
     * Retrieves {@code SerialType} from PostgreSQL serial column type.
     *
     * @param pgColumnType PostgreSQL serial column type; should be non-null.
     * @return {@code SerialType}
     */
    @Nonnull
    public static SerialType valueFrom(@Nonnull final String pgColumnType) {
        Objects.requireNonNull(pgColumnType, "pgColumnType cannot be null");
        final SerialType serialType = VALUES.get(pgColumnType);
        if (serialType == null) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "pgColumnType = '%s'", pgColumnType));
        }
        return serialType;
    }
}
