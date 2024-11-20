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

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.model.settings.ImportantParam;
import io.github.mfvanek.pg.model.settings.ParamNameAware;
import io.github.mfvanek.pg.model.settings.PgParam;
import io.github.mfvanek.pg.model.settings.PgParamImpl;
import io.github.mfvanek.pg.model.settings.ServerSpecification;
import io.github.mfvanek.pg.utils.QueryExecutors;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class ConfigurationMaintenanceOnHostImpl implements ConfigurationMaintenanceOnHost {

    /**
     * A connection to a specific host in the cluster.
     */
    private final PgConnection pgConnection;

    public ConfigurationMaintenanceOnHostImpl(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection, "pgConnection cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public PgHost getHost() {
        return pgConnection.getHost();
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
        return params.stream()
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    @Nonnull
    public PgParam getParamCurrentValue(@Nonnull final ParamNameAware paramName) {
        return getCurrentValue(paramName.getName());
    }

    @Nonnull
    private PgParam getCurrentValue(@Nonnull final String paramName) {
        final String sqlQuery = String.format(Locale.ROOT, "show %s;", paramName);
        final List<PgParam> params = QueryExecutors.executeQuery(pgConnection, sqlQuery, rs -> {
            final String currentValue = rs.getString(paramName);
            return PgParamImpl.of(paramName, currentValue);
        });
        return params.get(0);
    }
}
