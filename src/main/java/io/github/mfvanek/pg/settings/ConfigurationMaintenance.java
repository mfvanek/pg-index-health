/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.settings;

import javax.annotation.Nonnull;
import java.util.Set;

public interface ConfigurationMaintenance {

    @Nonnull
    Set<PgParam> getParamsWithDefaultValues(@Nonnull ServerSpecification specification);

    @Nonnull
    Set<PgParam> getParamsCurrentValues();

    @Nonnull
    PgParam getParamCurrentValue(@Nonnull ParamNameAware paramName);
}
