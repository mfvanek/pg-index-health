/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * A wrapper of standard {@code DataSource} interface with awareness of real host.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 * @see PgHost
 */
public interface PgConnection extends HostAware {

    /**
     * Gets a standard {@code DataSource} object to access the database.
     *
     * @return {@code DataSource}
     */
    @Nonnull
    DataSource getDataSource();
}
