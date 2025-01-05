/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.settings;

import io.github.mfvanek.pg.model.settings.PgParam;
import io.github.mfvanek.pg.model.settings.ServerSpecification;

import java.util.Set;
import javax.annotation.Nonnull;

/**
 * An abstraction for getting database configuration.
 *
 * @author Ivan Vakhrushev
 * @since 0.4.0
 */
public interface ConfigurationAware {

    @Nonnull
    Set<PgParam> getParamsWithDefaultValues(@Nonnull ServerSpecification specification);

    @Nonnull
    Set<PgParam> getParamsCurrentValues();
}
