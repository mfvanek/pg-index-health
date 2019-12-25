/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * A wrapper of standard DataSource interface with awareness of real host.
 */
public interface PgConnection extends HostAware {

    @Nonnull
    DataSource getDataSource();
}
