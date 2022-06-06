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

import io.github.mfvanek.pg.common.maintenance.AbstractMaintenance;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.settings.ImportantParam;
import io.github.mfvanek.pg.settings.ParamNameAware;
import io.github.mfvanek.pg.settings.PgParam;
import io.github.mfvanek.pg.settings.PgParamImpl;
import io.github.mfvanek.pg.settings.ServerSpecification;
import io.github.mfvanek.pg.utils.QueryExecutors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public class ConfigurationMaintenanceOnHostImpl extends AbstractMaintenance implements ConfigurationMaintenanceOnHost {

    public ConfigurationMaintenanceOnHostImpl(@Nonnull final PgConnection pgConnection) {
        super(pgConnection);
    }

    @Nonnull
    @Override
    public Set<PgParam> getParamsWithDefaultValues(@Nonnull final ServerSpecification specification) {
        // TODO get max_connections and calculate recommended values
        final Set<PgParam> params = new HashSet<>();
        for (final ImportantParam importantParam : ImportantParam.values()) {
            final PgParam currentValue = getParamCurrentValue(importantParam);
            if (currentValue.getValue().equals(importantParam.getDefaultValue())) {
                params.add(currentValue);
            }
        }
        return params;
    }

    @Override
    @Nonnull
    public Set<PgParam> getParamsCurrentValues() {
        final List<PgParam> params = QueryExecutors.executeQuery(pgConnection, "show all", rs -> {
            final String paramName = rs.getString("name");
            final String currentValue = rs.getString("setting");
            return PgParamImpl.of(paramName, currentValue);
        });
        return new HashSet<>(params);
    }

    @Override
    @Nonnull
    public PgParam getParamCurrentValue(@Nonnull final ParamNameAware paramName) {
        return getCurrentValue(paramName.getName());
    }

    @Nonnull
    private PgParam getCurrentValue(@Nonnull final String paramName) {
        final String sqlQuery = String.format("show %s;", paramName);
        final List<PgParam> params = QueryExecutors.executeQuery(pgConnection, sqlQuery, rs -> {
            final String currentValue = rs.getString(paramName);
            return PgParamImpl.of(paramName, currentValue);
        });
        return params.get(0);
    }
}
