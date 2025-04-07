/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.settings;

import javax.annotation.Nonnull;

/**
 * Deprecated for removal.
 *
 * @deprecated since 0.14.6
 */
@Deprecated(forRemoval = true)
public interface PgParam extends ParamNameAware {

    @Nonnull
    String getValue();
}
