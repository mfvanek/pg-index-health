/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

/**
 * Represents a context for running maintenance queries.
 *
 * @author Ivan Vakhrushev
 */
public class PgContext {

    /**
     * Default bloat percentage threshold.
     */
    public static final int DEFAULT_BLOAT_PERCENTAGE_THRESHOLD = 10;
    public static final String DEFAULT_SCHEMA_NAME = "public";

    private final String schemaName;
    private final int bloatPercentageThreshold;

    private PgContext(@Nonnull final String schemaName, int bloatPercentageThreshold) {
        this.schemaName = Validators.notBlank(schemaName, "schemaName").toLowerCase();
        this.bloatPercentageThreshold = Validators.argumentNotNegative(
                bloatPercentageThreshold, "bloatPercentageThreshold");
    }

    /**
     * Returns the specified schema name.
     *
     * @return schema name
     */
    @Nonnull
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Determines whether the specified schema is public or not.
     *
     * @return true if it is the public schema
     */
    public boolean isDefaultSchema() {
        return DEFAULT_SCHEMA_NAME.equalsIgnoreCase(schemaName);
    }

    /**
     * Returns the specified bloat percentage threshold.
     *
     * @return bloat percentage threshold
     */
    public int getBloatPercentageThreshold() {
        return bloatPercentageThreshold;
    }

    @Override
    public String toString() {
        return PgContext.class.getSimpleName() + '{' +
                "schemaName='" + schemaName + '\'' +
                ", bloatPercentageThreshold=" + bloatPercentageThreshold +
                '}';
    }

    /**
     * Complement the given object name with the specified schema name if it is necessary.
     *
     * @param objectName given object name
     * @return object name with schema for non default schemas
     */
    @Nonnull
    public String enrichWithSchema(@Nonnull final String objectName) {
        Validators.notBlank(objectName, "objectName");

        if (isDefaultSchema()) {
            return objectName;
        }

        final String prefix = schemaName + ".";
        if (objectName.toLowerCase().startsWith(prefix)) {
            return objectName;
        }

        return prefix + objectName;
    }

    /**
     * Creates {@code PgContext} for given schema with given bloat percentage threshold.
     *
     * @param schemaName               given database schema
     * @param bloatPercentageThreshold given bloat percentage threshold; should be greater or equals to zero
     * @return {@code PgContext}
     */
    @Nonnull
    public static PgContext of(@Nonnull final String schemaName, int bloatPercentageThreshold) {
        return new PgContext(schemaName, bloatPercentageThreshold);
    }

    /**
     * Creates {@code PgContext} for given schema with default bloat percentage threshold.
     *
     * @param schemaName given database schema
     * @return {@code PgContext}
     * @see PgContext#DEFAULT_BLOAT_PERCENTAGE_THRESHOLD
     */
    @Nonnull
    public static PgContext of(@Nonnull final String schemaName) {
        return of(schemaName, DEFAULT_BLOAT_PERCENTAGE_THRESHOLD);
    }

    /**
     * Creates {@code PgContext} for public schema with default bloat percentage threshold.
     *
     * @return {@code PgContext}
     * @see PgContext#DEFAULT_BLOAT_PERCENTAGE_THRESHOLD
     */
    @Nonnull
    public static PgContext ofPublic() {
        return of(DEFAULT_SCHEMA_NAME);
    }
}
