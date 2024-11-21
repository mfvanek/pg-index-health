/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.settings.maintenance;

import io.github.mfvanek.pg.connection.exception.PgSqlException;
import io.github.mfvanek.pg.core.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.model.settings.ImportantParam;
import io.github.mfvanek.pg.model.settings.PgParam;
import io.github.mfvanek.pg.model.settings.PgParamImpl;
import io.github.mfvanek.pg.model.settings.ServerSpecification;
import io.github.mfvanek.pg.model.units.MemoryUnit;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigurationMaintenanceOnHostImplTest extends DatabaseAwareTestBase {

    private final ConfigurationMaintenanceOnHost configurationMaintenance = new ConfigurationMaintenanceOnHostImpl(getPgConnection());

    @Test
    void getHostShouldReturnPrimary() {
        assertThat(configurationMaintenance.getHost())
            .isEqualTo(getHost());
    }

    @Test
    void getParamsWithDefaultValues() {
        final ServerSpecification specification = ServerSpecification.builder()
            .withCpuCores(2)
            .withMemoryAmount(2, MemoryUnit.GB)
            .withSSD()
            .build();
        final Set<PgParam> paramsWithDefaultValues = configurationMaintenance.getParamsWithDefaultValues(specification);
        assertThat(paramsWithDefaultValues)
            .hasSize(5)
            .extracting(PgParam::getName)
            .containsExactlyInAnyOrder("log_min_duration_statement", "idle_in_transaction_session_timeout", "statement_timeout", "effective_cache_size", "temp_file_limit");
    }

    @Test
    void getParamsCurrentValues() {
        final Set<PgParam> currentValues = configurationMaintenance.getParamsCurrentValues();
        assertThat(currentValues)
            .hasSizeGreaterThan(200)
            .isUnmodifiable();
        final Set<String> allParamNames = currentValues.stream()
            .map(PgParam::getName)
            .collect(Collectors.toUnmodifiableSet());
        for (final ImportantParam importantParam : ImportantParam.values()) {
            assertThat(allParamNames).contains(importantParam.getName());
        }
    }

    @Test
    void getParamCurrentValue() {
        final PgParam currentValue = configurationMaintenance.getParamCurrentValue(ImportantParam.LOG_MIN_DURATION_STATEMENT);
        assertThat(currentValue)
            .isNotNull()
            .extracting(PgParam::getValue)
            .isEqualTo(ImportantParam.LOG_MIN_DURATION_STATEMENT.getDefaultValue());
    }

    @Test
    void getCurrentValueForUnknownParam() {
        final PgParam pgParam = PgParamImpl.of("unknown_param", "");
        assertThatThrownBy(() -> configurationMaintenance.getParamCurrentValue(pgParam))
            .isInstanceOf(PgSqlException.class)
            .hasCauseInstanceOf(SQLException.class)
            .hasMessageContaining("unknown_param");
    }
}
