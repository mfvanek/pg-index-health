package com.mfvanek.pg.settings;

import com.mfvanek.pg.connection.PgConnectionImpl;
import com.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import static org.hamcrest.MatcherAssert.assertThat;
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
    }
}
