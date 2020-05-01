/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.table;

import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A set of diagnostics for collecting statistics about the health of tables.
 *
 * @author Ivan Vakhrushev
 * @see PgContext
 */
public interface TablesHealthAware {

    /**
     * Returns tables with potentially missing indexes in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of tables with potentially missing indexes
     */
    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns tables with potentially missing indexes in the specified schemas.
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of tables with potentially missing indexes
     */
    @Nonnull
    default List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getTablesWithMissingIndexes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns tables with potentially missing indexes in the public schema.
     *
     * @return list of tables with potentially missing indexes
     */
    @Nonnull
    default List<TableWithMissingIndex> getTablesWithMissingIndexes() {
        return getTablesWithMissingIndexes(PgContext.ofPublic());
    }

    /**
     * Returns tables without primary key on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of tables without primary key
     */
    @Nonnull
    List<Table> getTablesWithoutPrimaryKey(@Nonnull PgContext pgContext);

    /**
     * Returns tables without primary key on the current host in the specified schemas.
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of tables without primary key
     */
    @Nonnull
    default List<Table> getTablesWithoutPrimaryKey(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getTablesWithoutPrimaryKey)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns tables without primary key on the current host in the public schema.
     *
     * @return list of tables without primary key
     */
    @Nonnull
    default List<Table> getTablesWithoutPrimaryKey() {
        return getTablesWithoutPrimaryKey(PgContext.ofPublic());
    }

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
    @Nonnull
    List<TableWithBloat> getTablesWithBloat(@Nonnull PgContext pgContext);

    /**
     * Returns tables that are bloated on the current host in the specified schemas.
     * <p>
     * Note: The database user on whose behalf this method will be executed
     * have to have read permissions for the corresponding tables.
     * </p>
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of bloated tables
     */
    @Nonnull
    default List<TableWithBloat> getTablesWithBloat(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getTablesWithBloat)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns tables that are bloated on the current host in the public schema.
     * <p>
     * Note: The database user on whose behalf this method will be executed
     * have to have read permissions for the corresponding tables.
     * </p>
     *
     * @return list of bloated tables
     */
    @Nonnull
    default List<TableWithBloat> getTablesWithBloat() {
        return getTablesWithBloat(PgContext.ofPublic());
    }
}
