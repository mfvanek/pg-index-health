package com.mfvanek.pg.settings;

import com.mfvanek.pg.connection.PgConnection;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class ConfigurationMaintenanceImpl implements ConfigurationMaintenance {

    private final PgConnection pgConnection;

    public ConfigurationMaintenanceImpl(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection);
    }

    @Nonnull
    @Override
    public List<PgParam> getParamsWithDefaultValues(@Nonnull ServerSpecification specification) {
        return null;
    }
}
