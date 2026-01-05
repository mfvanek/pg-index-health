/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.host;

/**
 * Allows getting information about database host.
 *
 * @author Ivan Vakhrushev
 */
public interface HostAware {

    /**
     * Retrieves information about host in the cluster.
     *
     * @return {@code PgHost}
     * @see PgHost
     */
    PgHost getHost();
}
