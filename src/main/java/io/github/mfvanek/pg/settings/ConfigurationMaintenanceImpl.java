/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.settings;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.utils.QueryExecutor;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class ConfigurationMaintenanceImpl implements ConfigurationMaintenance {

    private final PgConnection pgConnection;

    public ConfigurationMaintenanceImpl(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection);
    }

    @Nonnull
    @Override
    public Set<PgParam> getParamsWithDefaultValues(@Nonnull ServerSpecification specification) {
        // TODO get max_connections and calculate recommended values
        final Set<PgParam> params = new HashSet<>();
        for (var importantParam : ImportantParam.values()) {
            var currentValue = getParamCurrentValue(importantParam);
            if (currentValue.getValue().equals(importantParam.getDefaultValue())) {
                params.add(currentValue);
            }
        }
        return params;
    }

    @Override
    @Nonnull
    public Set<PgParam> getParamsCurrentValues() {
        final var params = QueryExecutor.executeQuery(pgConnection, "show all", rs -> {
            final String paramName = rs.getString("name");
            final String currentValue = rs.getString("setting");
            return PgParamImpl.of(paramName, currentValue);
        });
        return Set.copyOf(params);
    }

    @Override
    @Nonnull
    public PgParam getParamCurrentValue(@Nonnull final ParamNameAware paramName) {
        return getCurrentValue(paramName.getName());
    }

    @Nonnull
    private PgParam getCurrentValue(@Nonnull final String paramName) {
        final String sqlQuery = String.format("show %s;", paramName);
        final var params = QueryExecutor.executeQuery(pgConnection, sqlQuery, rs -> {
            final String currentValue = rs.getString(paramName);
            return PgParamImpl.of(paramName, currentValue);
        });
        if (params.size() != 1) {
            throw new NoSuchElementException(paramName);
        }
        return params.get(0);
    }
}
