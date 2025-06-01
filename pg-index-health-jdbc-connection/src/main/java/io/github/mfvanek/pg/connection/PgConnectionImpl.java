/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.connection.exception.PgSqlException;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.connection.host.PgHostImpl;
import io.github.mfvanek.pg.connection.host.PgUrlParser;
import org.jspecify.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import javax.sql.DataSource;

/**
 * A standard implementation of {@code PgConnection} interface with awareness of real host.
 *
 * @author Ivan Vakhrushev
 * @see PgConnection
 */
public class PgConnectionImpl implements PgConnection {

    private final DataSource dataSource;
    private final PgHost host;

    private PgConnectionImpl(final DataSource dataSource, final PgHost host) {
        this.dataSource = validateDataSource(dataSource);
        this.host = Objects.requireNonNull(host, "host cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgHost getHost() {
        return host;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PgConnection)) {
            return false;
        }

        final PgConnection that = (PgConnection) other;
        return Objects.equals(host, that.getHost());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hash(host);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return PgConnectionImpl.class.getSimpleName() + '{' +
            "host=" + host +
            '}';
    }

    /**
     * Constructs a {@code PgConnection} object with given dataSource and host.
     *
     * @param dataSource a factory for connections to the physical database; should be non-null.
     * @param host       information about database host; should be non-null.
     * @return {@code PgConnection}
     * @see DataSource
     * @see PgHost
     */
    public static PgConnection of(final DataSource dataSource, final PgHost host) {
        return new PgConnectionImpl(dataSource, host);
    }

    /**
     * Constructs a {@code PgConnection} object with given dataSource and connection string.
     *
     * @param dataSource  a factory for connections to the physical database; should be non-null.
     * @param databaseUrl a connection string to the physical database; can be obtained from connection metadata.
     * @return {@code PgConnection} object
     * @see Connection#getMetaData()
     * @see java.sql.DatabaseMetaData
     * @since 0.14.2
     */
    public static PgConnection ofUrl(final DataSource dataSource, @Nullable final String databaseUrl) {
        final PgHost host;
        if (needToGetUrlFromMetaData(databaseUrl)) {
            try (Connection connection = validateDataSource(dataSource).getConnection()) {
                host = PgHostImpl.ofUrl(connection.getMetaData().getURL());
            } catch (SQLException ex) {
                throw new PgSqlException(ex);
            }
        } else {
            host = PgHostImpl.ofUrl(databaseUrl);
        }
        return new PgConnectionImpl(dataSource, host);
    }

    private static DataSource validateDataSource(final DataSource dataSource) {
        return Objects.requireNonNull(dataSource, "dataSource cannot be null");
    }

    private static boolean needToGetUrlFromMetaData(@Nullable final String databaseUrl) {
        return databaseUrl == null ||
            databaseUrl.isBlank() ||
            databaseUrl.startsWith(PgUrlParser.TESTCONTAINERS_PG_URL_PREFIX);
    }
}
