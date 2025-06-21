/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.connection.host.HostAware;
import io.github.mfvanek.pg.connection.host.PgHost;

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
     * Retrieves a standard {@code DataSource} object to access the database.
     *
     * @return {@code DataSource}
     */
    DataSource getDataSource();
}
