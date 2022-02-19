/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.table.maintenance;

import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import io.github.mfvanek.pg.table.TablesHealthAware;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * A set of diagnostics for collecting statistics about the health of tables on a specific host.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 * @see PgContext
 * @see TablesHealthAware
 */
public interface TablesMaintenanceOnHost extends TablesHealthAware, HostAware {

    /**
     * Returns tables with potentially missing indexes on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of tables with potentially missing indexes
     */
    @Override
    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns tables without primary key on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of tables without primary key
     */
    @Override
    @Nonnull
    List<Table> getTablesWithoutPrimaryKey(@Nonnull PgContext pgContext);

    /**
     * Returns tables that are bloated on the current host in the specified schema.
     * <p>
     * Note: The database user on whose behalf this method will be executed
     * have to have read permissions for the corresponding tables.
     * </p>
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of bloated tables
     */
    @Override
    @Nonnull
    List<TableWithBloat> getTablesWithBloat(@Nonnull PgContext pgContext);
}
