/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.settings.maintenance;

import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.settings.ImportantParam;
import io.github.mfvanek.pg.settings.PgParam;
import io.github.mfvanek.pg.settings.PgParamImpl;
import io.github.mfvanek.pg.settings.ServerSpecification;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigurationMaintenanceOnHostImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database()
            .withAdditionalStartupParameter(ImportantParam.LOCK_TIMEOUT.getName(), "1000");

    private final ConfigurationMaintenanceOnHost configurationMaintenance;

    ConfigurationMaintenanceOnHostImplTest() {
        super(embeddedPostgres.getTestDatabase());
        this.configurationMaintenance = new ConfigurationMaintenanceOnHostImpl(
                PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase()));
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
                .isNotNull()
                .hasSize(9);
        assertThat(paramsWithDefaultValues.stream().map(PgParam::getName).collect(toList()))
                .containsExactlyInAnyOrder("shared_buffers", "work_mem", "maintenance_work_mem", "random_page_cost", "log_min_duration_statement", "idle_in_transaction_session_timeout",
                        "statement_timeout", "effective_cache_size", "temp_file_limit");
    }

    @Test
    void getParamsCurrentValues() {
        final Set<PgParam> currentValues = configurationMaintenance.getParamsCurrentValues();
        assertThat(currentValues)
                .isNotNull()
                .hasSizeGreaterThan(200);
        final Set<String> allParamNames = currentValues.stream()
                .map(PgParam::getName)
                .collect(toSet());
        for (ImportantParam importantParam : ImportantParam.values()) {
            assertThat(allParamNames).contains(importantParam.getName());
        }
    }

    @Test
    void getParamCurrentValue() {
        final PgParam currentValue = configurationMaintenance.getParamCurrentValue(ImportantParam.LOG_MIN_DURATION_STATEMENT);
        assertThat(currentValue).isNotNull();
        assertThat(currentValue.getValue()).isEqualTo(ImportantParam.LOG_MIN_DURATION_STATEMENT.getDefaultValue());
    }

    @Test
    void getCurrentValueForUnknownParam() {
        final PgParam pgParam = PgParamImpl.of("unknown_param", "");
        assertThatThrownBy(() -> configurationMaintenance.getParamCurrentValue(pgParam))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("unknown_param");
    }
}
