/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.settings.maintenance;

import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.settings.ParamNameAware;
import io.github.mfvanek.pg.settings.PgParam;
import io.github.mfvanek.pg.settings.ServerSpecification;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * An entry point for working with database configuration on the specified host.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 */
public interface ConfigurationMaintenanceOnHost extends HostAware {

    @Nonnull
    Set<PgParam> getParamsWithDefaultValues(@Nonnull ServerSpecification specification);

    @Nonnull
    Set<PgParam> getParamsCurrentValues();

    @Nonnull
    PgParam getParamCurrentValue(@Nonnull ParamNameAware paramName);
}
