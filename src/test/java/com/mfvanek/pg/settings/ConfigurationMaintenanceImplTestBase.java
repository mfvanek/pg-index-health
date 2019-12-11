package com.mfvanek.pg.settings;

import com.mfvanek.pg.connection.PgConnectionImpl;
import com.mfvanek.pg.model.MemoryUnit;
import com.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

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
}
