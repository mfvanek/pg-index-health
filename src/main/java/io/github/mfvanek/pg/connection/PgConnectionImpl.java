/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.Objects;

public class PgConnectionImpl implements PgConnection {

    private final DataSource dataSource;
    private final PgHost host;

    private PgConnectionImpl(@Nonnull final DataSource dataSource, @Nonnull final PgHost host) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.host = Objects.requireNonNull(host);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public PgHost getHost() {
        return host;
    }

    @Nonnull
    public static PgConnection ofPrimary(@Nonnull final DataSource dataSource) {
        return new PgConnectionImpl(dataSource, PgHostImpl.ofPrimary());
    }

    @Nonnull
    public static PgConnection of(@Nonnull final DataSource dataSource, @Nonnull final PgHost host) {
        return new PgConnectionImpl(dataSource, host);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PgConnectionImpl that = (PgConnectionImpl) o;
        return Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host);
    }

    @Override
    public String toString() {
        return PgConnectionImpl.class.getSimpleName() + '{' +
                "host=" + host +
                '}';
    }
}
