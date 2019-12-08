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
    private PgParam getCurrentValue(@Nonnull final ImportantParam importantParam) {
        final String sqlQuery = String.format("show %s;", importantParam.getName());
        final var params = QueryExecutor.executeQuery(pgConnection, sqlQuery, rs -> {
            final String currentValue = rs.getString(importantParam.getName());
            return PgParamImpl.of(importantParam.getName(), currentValue);
        });
        if (params.size() != 1) {
            throw new NoSuchElementException(importantParam.getName());
        }
        return params.get(0);
    }
}
