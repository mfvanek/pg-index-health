/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.settings;

import io.github.mfvanek.pg.model.settings.validation.ParamValidators;
import io.github.mfvanek.pg.model.validation.Validators;

/**
 * Enumeration of PostgreSQL configuration parameters considered important for database performance and behavior.
 * <p>
 * Each constant represents a specific configuration parameter with its name and a default value.
 * This enum implements {@link ParamNameAware}, providing access to the parameter name.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * for (ImportantParam param : ImportantParam.values()) {
 *     System.out.println(param.getName() + " = " + param.getDefaultValue());
 * }
 * }</pre>
 *
 * @see ParamNameAware
 */
public enum ImportantParam implements ParamNameAware {

    /**
     * PostgreSQL parameter {@code shared_buffers}, with a default value of {@code 128MB}.
     */
    SHARED_BUFFERS("shared_buffers", "128MB"),

    /**
     * PostgreSQL parameter {@code work_mem}, with a default value of {@code 4MB}.
     */
    WORK_MEM("work_mem", "4MB"),

    /**
     * PostgreSQL parameter {@code maintenance_work_mem}, with a default value of {@code 64MB}.
     */
    MAINTENANCE_WORK_MEM("maintenance_work_mem", "64MB"),

    /**
     * PostgreSQL parameter {@code random_page_cost}, with a default value of {@code 4}.
     */
    RANDOM_PAGE_COST("random_page_cost", "4"),

    /**
     * PostgreSQL parameter {@code log_min_duration_statement}, with a default value of {@code -1}.
     */
    LOG_MIN_DURATION_STATEMENT("log_min_duration_statement", "-1"),

    /**
     * PostgreSQL parameter {@code idle_in_transaction_session_timeout}, with a default value of {@code 0}.
     */
    IDLE_IN_TRANSACTION_SESSION_TIMEOUT("idle_in_transaction_session_timeout", "0"),

    /**
     * PostgreSQL parameter {@code statement_timeout}, with a default value of {@code 0}.
     */
    STATEMENT_TIMEOUT("statement_timeout", "0"),

    /**
     * PostgreSQL parameter {@code lock_timeout}, with a default value of {@code 0}.
     */
    LOCK_TIMEOUT("lock_timeout", "0"),

    /**
     * PostgreSQL parameter {@code effective_cache_size}, with a default value of {@code 4GB}.
     */
    EFFECTIVE_CACHE_SIZE("effective_cache_size", "4GB"),

    /**
     * PostgreSQL parameter {@code temp_file_limit}, with a default value of {@code -1}.
     */
    TEMP_FILE_LIMIT("temp_file_limit", "-1");

    private final String name;
    private final String defaultValue;

    ImportantParam(final String name, final String defaultValue) {
        this.name = Validators.notBlank(name, "name");
        this.defaultValue = ParamValidators.paramValueNotNull(
            defaultValue, "defaultValue for '" + name + "' cannot be null");
    }

    /**
     * Returns the default value for this parameter.
     *
     * @return the non-null default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ImportantParam.class.getSimpleName() + '{' +
            "name='" + name + '\'' +
            ", defaultValue='" + defaultValue + '\'' +
            '}';
    }
}
