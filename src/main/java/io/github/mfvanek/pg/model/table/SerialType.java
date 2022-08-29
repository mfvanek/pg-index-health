/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * A mapping to PostgreSQL serial types.
 *
 * @see <a href="https://www.postgresql.org/docs/current/datatype-numeric.html#DATATYPE-SERIAL">Serial types</a>
 *
 * @author Ivan Vakhrushev
 * @since 0.6.2
 */
public enum SerialType {

    SMALL_SERIAL("smallserial"),
    SERIAL("serial"),
    BIG_SERIAL("bigserial");

    private static final Map<String, SerialType> VALUES = new HashMap<>();

    static {
        for (final SerialType serialType : values()) {
            VALUES.put(serialType.pgTypeName, serialType);
        }
    }

    private final String pgTypeName;

    SerialType(@Nonnull final String pgTypeName) {
        this.pgTypeName = Objects.requireNonNull(pgTypeName);
    }

    @Nonnull
    public String getPgTypeName() {
        return pgTypeName;
    }

    @Override
    public String toString() {
        return SerialType.class.getSimpleName() + '{' +
                "pgTypeName='" + pgTypeName + '\'' +
                '}';
    }

    @Nonnull
    public static SerialType valueFrom(@Nonnull final String pgType) {
        Objects.requireNonNull(pgType, "pgType cannot be null");
        final SerialType serialType = VALUES.get(pgType);
        if (serialType == null) {
            throw new IllegalArgumentException(String.format("pgType = '%s'", pgType));
        }
        return serialType;
    }
}
