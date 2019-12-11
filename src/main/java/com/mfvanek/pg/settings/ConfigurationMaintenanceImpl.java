/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.settings;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.utils.QueryExecutor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ConfigurationMaintenanceImpl implements ConfigurationMaintenance {

    private final PgConnection pgConnection;

    public ConfigurationMaintenanceImpl(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection);
    }

    @Nonnull
    @Override
    public List<PgParam> getParamsWithDefaultValues(@Nonnull ServerSpecification specification) {
        // TODO get max_connections and calculate recommended values
        final List<PgParam> params = new ArrayList<>();
        for (var importantParam : ImportantParam.values()) {
            var currentValue = getCurrentValue(importantParam);
            if (currentValue.getValue().equals(importantParam.getValue())) {
                params.add(currentValue);
            }
        }
        return params;
    }

    @Nonnull
    private PgParam getCurrentValue(@Nonnull final PgParam param) {
        return getCurrentValue(param.getName());
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
