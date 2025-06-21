/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.context;

import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Locale;
import java.util.Objects;

/**
 * An immutable representation of a context for running maintenance queries.
 *
 * @author Ivan Vakhrushev
 */
public final class PgContext {

    /**
     * Default bloat percentage threshold.
     */
    public static final double DEFAULT_BLOAT_PERCENTAGE_THRESHOLD = 10.0;
    /**
     * Default schema name.
     */
    public static final String DEFAULT_SCHEMA_NAME = "public";
    /**
     * Default sequence remaining values percentage threshold.
     */
    public static final double DEFAULT_REMAINING_PERCENTAGE_THRESHOLD = 10.0;

    private final String schemaName;
    private final double bloatPercentageThreshold;
    private final double remainingPercentageThreshold;

    private PgContext(final String schemaName, final double bloatPercentageThreshold, final double remainingPercentageThreshold) {
        this.schemaName = Validators.notBlank(schemaName, "schemaName").toLowerCase(Locale.ROOT);
        this.bloatPercentageThreshold = Validators.validPercent(bloatPercentageThreshold, "bloatPercentageThreshold");
        this.remainingPercentageThreshold = Validators.validPercent(remainingPercentageThreshold, "remainingPercentageThreshold");
    }

    /**
     * Returns the specified schema name.
     *
     * @return schema name
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Determines whether the specified schema is default or not.
     *
     * @return true if it is the default schema
     */
    public boolean isDefaultSchema() {
        return DEFAULT_SCHEMA_NAME.equalsIgnoreCase(schemaName);
    }

    /**
     * Returns the specified bloat percentage threshold.
     *
     * @return bloat percentage threshold
     */
    public double getBloatPercentageThreshold() {
        return bloatPercentageThreshold;
    }

    /**
     * Returns the specified remaining percentage threshold.
     *
     * @return remaining percentage threshold
     */
    public double getRemainingPercentageThreshold() {
        return remainingPercentageThreshold;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return PgContext.class.getSimpleName() + '{' +
            "schemaName='" + schemaName + '\'' +
            ", bloatPercentageThreshold=" + bloatPercentageThreshold +
            ", remainingPercentageThreshold=" + remainingPercentageThreshold +
            '}';
    }

    /**
     * Complement the given object (table or index) name with the specified schema name if it is necessary.
     *
     * @param objectName given object name
     * @return object name with schema for non default schemas
     */
    public String enrichWithSchema(final String objectName) {
        Validators.notBlank(objectName, "objectName");

        if (isDefaultSchema()) {
            return objectName;
        }

        return enrichWithSchemaIfNeed(objectName);
    }

    private String enrichWithSchemaIfNeed(final String objectName) {
        final String prefix = schemaName + ".";
        if (objectName.toLowerCase(Locale.ROOT).startsWith(prefix)) {
            return objectName;
        }

        return prefix + objectName;
    }

    /**
     * Creates a {@code PgContext} for the given schema with the specified bloat percentage threshold
     * and remaining percentage threshold.
     *
     * @param schemaName                   the given database schema name
     * @param bloatPercentageThreshold     the specified bloat percentage threshold; should be greater than or equal to zero
     * @param remainingPercentageThreshold the specified remaining percentage threshold
     * @return {@code PgContext}
     */
    public static PgContext of(final String schemaName,
                               final double bloatPercentageThreshold,
                               final double remainingPercentageThreshold) {
        return new PgContext(schemaName, bloatPercentageThreshold, remainingPercentageThreshold);
    }

    /**
     * Creates {@code PgContext} for given schema with given bloat percentage threshold.
     *
     * @param schemaName               given database schema
     * @param bloatPercentageThreshold given bloat percentage threshold; should be greater or equals to zero
     * @return {@code PgContext}
     * @see PgContext#DEFAULT_REMAINING_PERCENTAGE_THRESHOLD
     */
    public static PgContext of(final String schemaName,
                               final double bloatPercentageThreshold) {
        return new PgContext(schemaName, bloatPercentageThreshold, DEFAULT_REMAINING_PERCENTAGE_THRESHOLD);
    }

    /**
     * Creates {@code PgContext} for given schema with default bloat percentage threshold.
     *
     * @param schemaName given database schema
     * @return {@code PgContext}
     * @see PgContext#DEFAULT_BLOAT_PERCENTAGE_THRESHOLD
     */
    public static PgContext of(final String schemaName) {
        return of(schemaName, DEFAULT_BLOAT_PERCENTAGE_THRESHOLD);
    }

    /**
     * Creates {@code PgContext} for default schema with default bloat percentage threshold.
     *
     * @return {@code PgContext}
     * @see PgContext#DEFAULT_BLOAT_PERCENTAGE_THRESHOLD
     * @see PgContext#DEFAULT_REMAINING_PERCENTAGE_THRESHOLD
     * @since 0.15.0
     */
    public static PgContext ofDefault() {
        return of(DEFAULT_SCHEMA_NAME);
    }

    /**
     * Complement the given object name with the specified schema name if it is necessary.
     *
     * @param objectName the name of the object to be enriched with schema information; must not be {@code null}
     * @param pgContext  the schema context to enrich object name; must be non-null.
     * @return the fully qualified object name with schema information
     * @since 0.14.3
     */
    public static String enrichWith(final String objectName, final PgContext pgContext) {
        Objects.requireNonNull(pgContext, "pgContext cannot be null");
        return pgContext.enrichWithSchema(objectName);
    }
}
