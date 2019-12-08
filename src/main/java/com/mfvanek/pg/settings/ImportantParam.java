package com.mfvanek.pg.settings;

import javax.annotation.Nonnull;
import java.util.Objects;

public enum ImportantParam {

    SHARED_BUFFERS("shared_buffers", "128MB"),
    WORK_MEM("work_mem", "4MB"),
    MAINTENANCE_WORK_MEM("maintenance_work_mem", "64MB"),
    RANDOM_PAGE_COST("random_page_cost", "4.0"),
    LOG_MIN_DURATION_STATEMENT("log_min_duration_statement", "-1"),
    IDLE_IN_TRANSACTION_SESSION_TIMEOUT("idle_in_transaction_session_timeout", "0"),
    STATEMENT_TIMEOUT("statement_timeout", "0"),
    LOCK_TIMEOUT("lock_timeout", "0"),
    EFFECTIVE_CACHE_SIZE("effective_cache_size", "4GB"),
    TEMP_FILE_LIMIT("temp_file_limit", "-1");

    final String paramName;
    final String defaultValue;

    ImportantParam(@Nonnull final String paramName, @Nonnull final String defaultValue) {
        this.paramName = Objects.requireNonNull(paramName);
        this.defaultValue = Objects.requireNonNull(defaultValue);
    }

    public String getParamName() {
        return paramName;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return ImportantParam.class.getSimpleName() + '{' +
                "paramName='" + paramName + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                '}';
    }
}
