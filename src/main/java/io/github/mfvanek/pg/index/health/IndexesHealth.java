/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health;

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
 * An entry point for collecting and managing statistics
 * about the health of tables and indexes on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @see PgContext
 */
public interface IndexesHealth {

    /**
     * Returns invalid (broken) indexes to be deleted or re-indexed on the master host in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of invalid indexes
     * @see Index
     */
    @Nonnull
    List<Index> getInvalidIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns invalid (broken) indexes to be deleted or re-indexed on the master host in the specified schemas.
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
     * Returns invalid (broken) indexes to be deleted or re-indexed on the master host in the public schema.
     *
     * @return list of invalid indexes
     * @see Index
     */
    @Nonnull
    default List<Index> getInvalidIndexes() {
        return getInvalidIndexes(PgContext.ofPublic());
    }

    @Nonnull
    List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getDuplicatedIndexes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Nonnull
    default List<DuplicatedIndexes> getDuplicatedIndexes() {
        return getDuplicatedIndexes(PgContext.ofPublic());
    }

    @Nonnull
    List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getIntersectedIndexes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Nonnull
    default List<DuplicatedIndexes> getIntersectedIndexes() {
        return getIntersectedIndexes(PgContext.ofPublic());
    }

    @Nonnull
    List<UnusedIndex> getUnusedIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<UnusedIndex> getUnusedIndexes(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getUnusedIndexes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Nonnull
    default List<UnusedIndex> getUnusedIndexes() {
        return getUnusedIndexes(PgContext.ofPublic());
    }

    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull PgContext pgContext);

    @Nonnull
    default List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getForeignKeysNotCoveredWithIndex)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Nonnull
    default List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        return getForeignKeysNotCoveredWithIndex(PgContext.ofPublic());
    }

    @Nonnull
    List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull PgContext pgContext);

    @Nonnull
    default List<TableWithMissingIndex> getTablesWithMissingIndexes(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getTablesWithMissingIndexes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Nonnull
    default List<TableWithMissingIndex> getTablesWithMissingIndexes() {
        return getTablesWithMissingIndexes(PgContext.ofPublic());
    }

    @Nonnull
    List<Table> getTablesWithoutPrimaryKey(@Nonnull PgContext pgContext);

    @Nonnull
    default List<Table> getTablesWithoutPrimaryKey(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getTablesWithoutPrimaryKey)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Nonnull
    default List<Table> getTablesWithoutPrimaryKey() {
        return getTablesWithoutPrimaryKey(PgContext.ofPublic());
    }

    /**
     * Returns indexes in the specified schema on master host that contain null values.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of indexes with null values
     * @see PgContext
     * @see IndexWithNulls
     */
    @Nonnull
    List<IndexWithNulls> getIndexesWithNullValues(@Nonnull PgContext pgContext);

    @Nonnull
    default List<IndexWithNulls> getIndexesWithNullValues(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getIndexesWithNullValues)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns indexes in the public schema on master host that contain null values.
     *
     * @return list of indexes with null values
     * @see PgContext
     * @see IndexWithNulls
     */
    @Nonnull
    default List<IndexWithNulls> getIndexesWithNullValues() {
        return getIndexesWithNullValues(PgContext.ofPublic());
    }

    /**
     * Returns bloated indexes in the specified schema on master host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of bloated indexes
     * @see PgContext
     * @see IndexWithBloat
     */
    @Nonnull
    List<IndexWithBloat> getIndexesWithBloat(@Nonnull PgContext pgContext);

    @Nonnull
    default List<IndexWithBloat> getIndexesWithBloat(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getIndexesWithBloat)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns bloated indexes in the public schema on master host.
     *
     * @return list of bloated indexes
     * @see PgContext
     * @see IndexWithBloat
     */
    @Nonnull
    default List<IndexWithBloat> getIndexesWithBloat() {
        return getIndexesWithBloat(PgContext.ofPublic());
    }

    /**
     * Returns bloated tables in the specified schema on master host.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of bloated tables
     * @see PgContext
     * @see TableWithBloat
     */
    @Nonnull
    List<TableWithBloat> getTablesWithBloat(@Nonnull PgContext pgContext);

    @Nonnull
    default List<TableWithBloat> getTablesWithBloat(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getTablesWithBloat)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns bloated tables in the public schema on master host.
     *
     * @return list of bloated tables
     * @see PgContext
     * @see TableWithBloat
     */
    @Nonnull
    default List<TableWithBloat> getTablesWithBloat() {
        return getTablesWithBloat(PgContext.ofPublic());
    }

    /**
     * Reset all statistics counters on all hosts in the cluster to zero.
     * <p>
     * It is safe running this method on your database.
     * It just reset counters without any impact on performance.
     *
     * @see io.github.mfvanek.pg.index.maintenance.StatisticsMaintenance
     */
    void resetStatistics();
}
