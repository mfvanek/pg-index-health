/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.model.DuplicatedIndexes;
import io.github.mfvanek.pg.model.ForeignKey;
import io.github.mfvanek.pg.model.Index;
import io.github.mfvanek.pg.model.IndexWithBloat;
import io.github.mfvanek.pg.model.IndexWithNulls;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.Table;
import io.github.mfvanek.pg.model.TableWithBloat;
import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.model.UnusedIndex;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A set of diagnostics for collecting statistics about the health of tables and indexes on a specific host.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 * @see PgContext
 */
public interface IndexMaintenance extends HostAware {

    /**
     * Returns invalid (broken) indexes to be deleted or re-indexed on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of invalid indexes
     * @see Index
     */
    @Nonnull
    List<Index> getInvalidIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns invalid (broken) indexes to be deleted or re-indexed on the current host in the specified schemas.
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of invalid indexes
     * @see Index
     */
    @Nonnull
    default List<Index> getInvalidIndexes(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getInvalidIndexes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns invalid (broken) indexes to be deleted or re-indexed on the current host in the public schema.
     *
     * @return list of invalid indexes
     * @see Index
     */
    @Nonnull
    default List<Index> getInvalidIndexes() {
        return getInvalidIndexes(PgContext.ofPublic());
    }

    /**
     * Returns duplicated (completely identical) indexes (candidates for deletion)
     * on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of duplicated indexes
     */
    @Nonnull
    List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns duplicated (completely identical) indexes (candidates for deletion)
     * on the current host in the specified schemas.
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of duplicated indexes
     */
    @Nonnull
    default List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getDuplicatedIndexes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns duplicated (completely identical) indexes (candidates for deletion)
     * on the current host in the public schema.
     *
     * @return list of duplicated indexes
     */
    @Nonnull
    default List<DuplicatedIndexes> getDuplicatedIndexes() {
        return getDuplicatedIndexes(PgContext.ofPublic());
    }

    /**
     * Returns intersected indexes (partially identical, candidates for deletion)
     * on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of intersected indexes
     */
    @Nonnull
    List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns intersected indexes (partially identical, candidates for deletion)
     * on the current host in the specified schemas.
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of intersected indexes
     */
    @Nonnull
    default List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getIntersectedIndexes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns intersected indexes (partially identical, candidates for deletion)
     * on the current host in the public schema.
     *
     * @return list of intersected indexes
     */
    @Nonnull
    default List<DuplicatedIndexes> getIntersectedIndexes() {
        return getIntersectedIndexes(PgContext.ofPublic());
    }

    /**
     * Returns potentially unused indexes (candidates for deletion) on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of potentially unused indexes
     */
    @Nonnull
    List<UnusedIndex> getPotentiallyUnusedIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns potentially unused indexes (candidates for deletion) on the current host in the specified schemas.
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of potentially unused indexes
     */
    @Nonnull
    default List<UnusedIndex> getPotentiallyUnusedIndexes(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getPotentiallyUnusedIndexes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns potentially unused indexes (candidates for deletion) on the current host in the public schema.
     *
     * @return list of potentially unused indexes
     */
    @Nonnull
    default List<UnusedIndex> getPotentiallyUnusedIndexes() {
        return getPotentiallyUnusedIndexes(PgContext.ofPublic());
    }

    /**
     * Returns foreign keys without associated indexes (potential performance degradation)
     * on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of foreign keys without associated indexes
     */
    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull PgContext pgContext);

    /**
     * Returns foreign keys without associated indexes (potential performance degradation)
     * on the current host in the specified schemas.
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of foreign keys without associated indexes
     */
    @Nonnull
    default List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getForeignKeysNotCoveredWithIndex)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns foreign keys without associated indexes (potential performance degradation)
     * on the current host in the public schema.
     *
     * @return list of foreign keys without associated indexes
     */
    @Nonnull
    default List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        return getForeignKeysNotCoveredWithIndex(PgContext.ofPublic());
    }

    /**
     * Returns tables with potentially missing indexes (potential performance degradation)
     * on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of tables with potentially missing indexes
     */
    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns tables with potentially missing indexes (potential performance degradation)
     * on the current host in the specified schemas.
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
     * Returns tables with potentially missing indexes (potential performance degradation)
     * on the current host in the public schema.
     *
     * @return list of tables with potentially missing indexes
     */
    @Nonnull
    default List<TableWithMissingIndex> getTablesWithMissingIndexes() {
        return getTablesWithMissingIndexes(PgContext.ofPublic());
    }

    /**
     * Returns tables without primary key on the current host in the specified schema.
     * <p>
     * Tables without primary key might become a huge problem when bloat occurs
     * because pg_repack will not be able to process them.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of tables without primary key
     */
    @Nonnull
    List<Table> getTablesWithoutPrimaryKey(@Nonnull PgContext pgContext);

    /**
     * Returns tables without primary key on the current host in the public schema.
     * <p>
     * Tables without primary key might become a huge problem when bloat occurs
     * because pg_repack will not be able to process them.
     *
     * @return list of tables without primary key
     */
    @Nonnull
    default List<Table> getTablesWithoutPrimaryKey() {
        return getTablesWithoutPrimaryKey(PgContext.ofPublic());
    }

    /**
     * Returns indexes that contain null values on the current host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of indexes with null values
     */
    @Nonnull
    List<IndexWithNulls> getIndexesWithNullValues(@Nonnull PgContext pgContext);

    /**
     * Returns indexes that contain null values on the current host in the public schema.
     *
     * @return list of indexes with null values
     */
    @Nonnull
    default List<IndexWithNulls> getIndexesWithNullValues() {
        return getIndexesWithNullValues(PgContext.ofPublic());
    }

    /**
     * Returns indexes that are bloated on the current host in the specified schema.
     * <p>
     * Note: The database user on whose behalf this method will be executed
     * have to have read permissions for the corresponding tables.
     * </p>
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of bloated indexes
     */
    @Nonnull
    List<IndexWithBloat> getIndexesWithBloat(@Nonnull PgContext pgContext);

    /**
     * Returns indexes that are bloated on the current host in the public schema.
     * <p>
     * Note: The database user on whose behalf this method will be executed
     * have to have read permissions for the corresponding tables.
     * </p>
     *
     * @return list of bloated indexes
     */
    @Nonnull
    default List<IndexWithBloat> getIndexesWithBloat() {
        return getIndexesWithBloat(PgContext.ofPublic());
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
