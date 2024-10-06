/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.index.utils.DuplicatedIndexesParser;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A representation of duplicated foreign keys in a database.
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 */
@Immutable
public class DuplicatedForeignKeys implements DbObject, TableNameAware {

    private final List<ForeignKey> foreignKeys;
    private final List<String> foreignKeysNames;

    private DuplicatedForeignKeys(@Nonnull final List<ForeignKey> foreignKeys) {
        final List<ForeignKey> defensiveCopy = List.copyOf(Objects.requireNonNull(foreignKeys, "foreignKeys cannot be null"));
        Validators.validateThatTableIsTheSame(defensiveCopy);
        this.foreignKeys = defensiveCopy;
        this.foreignKeysNames = this.foreignKeys.stream()
            .map(ForeignKey::getConstraintName)
            .collect(Collectors.toUnmodifiableList());
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final String getName() {
        return String.join(",", foreignKeysNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getTableName() {
        return foreignKeys.get(0).getTableName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof DuplicatedForeignKeys)) {
            return false;
        }

        final DuplicatedForeignKeys that = (DuplicatedForeignKeys) other;
        return Objects.equals(foreignKeys, that.foreignKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hash(foreignKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return DuplicatedForeignKeys.class.getSimpleName() + '{' +
            "tableName='" + getTableName() + '\'' +
            ", foreignKeys=" + foreignKeys +
            '}';
    }

    /**
     * Constructs an {@code DuplicatedForeignKeys} object from given list of foreign keys.
     *
     * @param foreignKeys list of duplicated foreign keys; should be non-null.
     * @return {@code DuplicatedForeignKeys}
     */
    @Nonnull
    public static DuplicatedForeignKeys of(@Nonnull final List<ForeignKey> foreignKeys) {
        return new DuplicatedForeignKeys(foreignKeys);
    }

    /**
     * Constructs an {@code DuplicatedForeignKeys} object from given foreign keys.
     *
     * @param firstForeignKey  first foreign key; should be non-null.
     * @param secondForeignKey second foreign key; should be non-null.
     * @param otherForeignKeys other foreign keys.
     * @return {@code DuplicatedForeignKeys}
     */
    @Nonnull
    public static DuplicatedForeignKeys of(@Nonnull final ForeignKey firstForeignKey,
                                           @Nonnull final ForeignKey secondForeignKey,
                                           @Nonnull final ForeignKey... otherForeignKeys) {
        return new DuplicatedForeignKeys(DuplicatedIndexesParser.combine(firstForeignKey, secondForeignKey, otherForeignKeys));
    }
}
