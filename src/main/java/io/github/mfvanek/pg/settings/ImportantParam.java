/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.settings;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

public enum ImportantParam implements ParamNameAware {

    SHARED_BUFFERS("shared_buffers", "128MB"),
    WORK_MEM("work_mem", "4MB"),
    MAINTENANCE_WORK_MEM("maintenance_work_mem", "64MB"),
    RANDOM_PAGE_COST("random_page_cost", "4"),
    LOG_MIN_DURATION_STATEMENT("log_min_duration_statement", "-1"),
    IDLE_IN_TRANSACTION_SESSION_TIMEOUT("idle_in_transaction_session_timeout", "0"),
    STATEMENT_TIMEOUT("statement_timeout", "0"),
    LOCK_TIMEOUT("lock_timeout", "0"),
    EFFECTIVE_CACHE_SIZE("effective_cache_size", "4GB"),
    TEMP_FILE_LIMIT("temp_file_limit", "-1");

    private final String name;
    private final String defaultValue;

    ImportantParam(@Nonnull final String name, @Nonnull final String defaultValue) {
        this.name = Validators.notBlank(name, "name");
        this.defaultValue = Validators.paramValueNotNull(
                defaultValue, "defaultValue for '" + name + "\' cannot be null");
    }

    @Nonnull
    public String getDefaultValue() {
        return defaultValue;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ImportantParam.class.getSimpleName() + '{' +
                "name='" + name + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }
}
