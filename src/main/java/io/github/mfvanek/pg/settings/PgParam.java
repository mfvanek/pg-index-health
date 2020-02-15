/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.settings;

import javax.annotation.Nonnull;

public interface PgParam extends ParamNameAware {

    @Nonnull
    String getValue();
}
