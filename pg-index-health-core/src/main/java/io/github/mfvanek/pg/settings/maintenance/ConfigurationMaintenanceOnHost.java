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

import io.github.mfvanek.pg.host.HostAware;
import io.github.mfvanek.pg.settings.ConfigurationAware;
import io.github.mfvanek.pg.settings.ParamNameAware;
import io.github.mfvanek.pg.settings.PgParam;
import io.github.mfvanek.pg.settings.ServerSpecification;

import java.util.Set;
import javax.annotation.Nonnull;

/**
 * An entry point for working with database configuration on the specified host.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 * @see ConfigurationAware
 */
public interface ConfigurationMaintenanceOnHost extends ConfigurationAware, HostAware {

    @Override
    @Nonnull
    Set<PgParam> getParamsWithDefaultValues(@Nonnull ServerSpecification specification);

    @Override
    @Nonnull
    Set<PgParam> getParamsCurrentValues();

    @Nonnull
    PgParam getParamCurrentValue(@Nonnull ParamNameAware paramName);
}
