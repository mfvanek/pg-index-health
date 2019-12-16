/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.settings;

import com.mfvanek.pg.connection.PgConnectionImpl;
import com.mfvanek.pg.model.MemoryUnit;
import com.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class ConfigurationMaintenanceImplTestBase extends DatabaseAwareTestBase {

    private final ConfigurationMaintenance configurationMaintenance;

    ConfigurationMaintenanceImplTestBase(@Nonnull final DataSource dataSource) {
        super(dataSource);
        this.configurationMaintenance = new ConfigurationMaintenanceImpl(
                PgConnectionImpl.ofMaster(dataSource));
    }

    @Test
    void getParamsWithDefaultValues() {
        final var specification = ServerSpecification.builder()
                .withCpuCores(2)
                .withMemoryAmount(2, MemoryUnit.GB)
                .withSSD()
                .build();
        final var paramsWithDefaultValues = configurationMaintenance.getParamsWithDefaultValues(specification);
        assertNotNull(paramsWithDefaultValues);
        assertThat(paramsWithDefaultValues, hasSize(9));
        assertThat(paramsWithDefaultValues.stream()
                .map(PgParam::getName)
                .collect(toList()), containsInAnyOrder(
                "shared_buffers",
                "work_mem",
                "maintenance_work_mem",
                "random_page_cost",
                "log_min_duration_statement",
                "idle_in_transaction_session_timeout",
                "statement_timeout",
                "effective_cache_size",
                "temp_file_limit")
        );
    }

    @Test
    void getParamsCurrentValues() {
        final var currentValues = configurationMaintenance.getParamsCurrentValues();
        assertNotNull(currentValues);
        assertThat(currentValues, hasSize(greaterThan(200)));
        final var allParamNames = currentValues.stream()
                .map(PgParam::getName)
                .collect(toSet());
        for (var importantParam : ImportantParam.values()) {
            assertThat(allParamNames, hasItem(importantParam.getName()));
        }
    }

    @Test
    void getParamCurrentValue() {
        final var currentValue = configurationMaintenance.getParamCurrentValue(ImportantParam.LOG_MIN_DURATION_STATEMENT);
        assertNotNull(currentValue);
        assertEquals(ImportantParam.LOG_MIN_DURATION_STATEMENT.getDefaultValue(), currentValue.getValue());
    }

    @Test
    void getCurrentValueForUnknownParam() {
        assertThrows(RuntimeException.class, () -> configurationMaintenance.getParamCurrentValue(PgParamImpl.of("unknown_param", "")));
    }
}
