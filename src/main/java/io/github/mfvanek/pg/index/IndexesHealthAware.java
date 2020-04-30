/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index;

import io.github.mfvanek.pg.model.DuplicatedIndexes;
import io.github.mfvanek.pg.model.ForeignKey;
import io.github.mfvanek.pg.model.Index;
import io.github.mfvanek.pg.model.IndexWithBloat;
import io.github.mfvanek.pg.model.IndexWithNulls;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.UnusedIndex;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A set of diagnostics for collecting statistics about the health of indexes.
 *
 * @author Ivan Vakhrushev
 * @see PgContext
 */
public interface IndexesHealthAware {

    /**
     * Returns invalid (broken) indexes in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of invalid indexes
     * @see Index
     */
    @Nonnull
    List<Index> getInvalidIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns invalid (broken) indexes in the specified schemas.
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
     * Returns invalid (broken) indexes in the public schema.
     *
     * @return list of invalid indexes
     * @see Index
     */
    @Nonnull
    default List<Index> getInvalidIndexes() {
        return getInvalidIndexes(PgContext.ofPublic());
    }

    /**
     * Returns duplicated (completely identical) indexes in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of duplicated indexes
     */
    @Nonnull
    List<DuplicatedIndexes> getDuplicatedIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns duplicated (completely identical) indexes in the specified schemas.
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
     * Returns duplicated (completely identical) indexes in the public schema.
     *
     * @return list of duplicated indexes
     */
    @Nonnull
    default List<DuplicatedIndexes> getDuplicatedIndexes() {
        return getDuplicatedIndexes(PgContext.ofPublic());
    }

    /**
     * Returns intersected indexes (partially identical) in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of intersected indexes
     */
    @Nonnull
    List<DuplicatedIndexes> getIntersectedIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns intersected indexes (partially identical) in the specified schemas.
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
     * Returns intersected indexes (partially identical) in the public schema.
     *
     * @return list of intersected indexes
     */
    @Nonnull
    default List<DuplicatedIndexes> getIntersectedIndexes() {
        return getIntersectedIndexes(PgContext.ofPublic());
    }

    /**
     * Returns unused indexes in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of unused indexes
     */
    @Nonnull
    List<UnusedIndex> getUnusedIndexes(@Nonnull PgContext pgContext);

    /**
     * Returns unused indexes in the specified schemas.
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of unused indexes
     */
    @Nonnull
    default List<UnusedIndex> getUnusedIndexes(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getUnusedIndexes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns unused indexes in the public schema.
     *
     * @return list of unused indexes
     */
    @Nonnull
    default List<UnusedIndex> getUnusedIndexes() {
        return getUnusedIndexes(PgContext.ofPublic());
    }

    /**
     * Returns foreign keys without associated indexes in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of foreign keys without associated indexes
     */
    @Nonnull
    List<ForeignKey> getForeignKeysNotCoveredWithIndex(@Nonnull PgContext pgContext);

    /**
     * Returns foreign keys without associated indexes in the specified schemas.
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
     * Returns foreign keys without associated indexes in the public schema.
     *
     * @return list of foreign keys without associated indexes
     */
    @Nonnull
    default List<ForeignKey> getForeignKeysNotCoveredWithIndex() {
        return getForeignKeysNotCoveredWithIndex(PgContext.ofPublic());
    }

    /**
     * Returns indexes that contain null values in the specified schema.
     *
     * @param pgContext {@code PgContext} with the specified schema
     * @return list of indexes with null values
     */
    @Nonnull
    List<IndexWithNulls> getIndexesWithNullValues(@Nonnull PgContext pgContext);

    /**
     * Returns indexes that contain null values in the specified schemas.
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of indexes with null values
     */
    @Nonnull
    default List<IndexWithNulls> getIndexesWithNullValues(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getIndexesWithNullValues)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns indexes that contain null values in the public schema.
     *
     * @return list of indexes with null values
     */
    @Nonnull
    default List<IndexWithNulls> getIndexesWithNullValues() {
        return getIndexesWithNullValues(PgContext.ofPublic());
    }

    /**
     * Returns indexes that are bloated in the specified schema.
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
     * Returns indexes that are bloated in the specified schemas.
     * <p>
     * Note: The database user on whose behalf this method will be executed
     * have to have read permissions for the corresponding tables.
     * </p>
     *
     * @param pgContexts a set of contexts specifying schemas
     * @return list of bloated indexes
     */
    @Nonnull
    default List<IndexWithBloat> getIndexesWithBloat(@Nonnull Collection<PgContext> pgContexts) {
        return pgContexts.stream()
                .map(this::getIndexesWithBloat)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Returns indexes that are bloated in the public schema.
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
}
