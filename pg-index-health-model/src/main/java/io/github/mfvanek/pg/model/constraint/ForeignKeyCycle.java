/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.index.utils.DuplicatedIndexesParser;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An immutable representation of a cycle of foreign keys between two or more tables in a database.
 * <p>
 * Foreign keys are ordered along the cycle: each foreign key references the table of the next foreign key in the list,
 * and the last foreign key references the table of the first one.
 * All tables of the cycle are equal: the first table is only the canonical start of the cycle and has no special meaning.
 * </p>
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 * @see ConstraintsAware
 * @since 0.50.0
 */
public final class ForeignKeyCycle implements DbObject, TableNameAware, ConstraintsAware, Comparable<ForeignKeyCycle> {

    /**
     * Represents the field name used to store information about foreign keys that form a cycle.
     */
    public static final String FOREIGN_KEYS_FIELD = "foreignKeys";

    private final List<ForeignKey> foreignKeys;
    private final List<String> foreignKeysNames;

    private ForeignKeyCycle(final Collection<ForeignKey> foreignKeys) {
        final List<ForeignKey> defensiveCopy = List.copyOf(Objects.requireNonNull(foreignKeys, FOREIGN_KEYS_FIELD + " cannot be null"));
        if (defensiveCopy.size() < 2) {
            throw new IllegalArgumentException(FOREIGN_KEYS_FIELD + " should contain at least two items");
        }
        final Set<String> distinctTables = defensiveCopy.stream()
            .map(ForeignKey::getTableName)
            .collect(Collectors.toUnmodifiableSet());
        if (distinctTables.size() != defensiveCopy.size()) {
            throw new IllegalArgumentException("Table name must not be repeated within a cycle");
        }
        this.foreignKeys = defensiveCopy;
        this.foreignKeysNames = this.foreignKeys.stream()
            .map(ForeignKey::getConstraintName)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return String.join(",", foreignKeysNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgObjectType getObjectType() {
        return PgObjectType.CONSTRAINT;
    }

    /**
     * Retrieves the name of the table from which the cycle starts.
     * <p>
     * This is only the canonical start of the cycle. To get all tables of the cycle use {@link #getTableNames()}.
     * </p>
     *
     * @return name of the first table in the cycle
     */
    @Override
    public String getTableName() {
        return foreignKeys.get(0).getTableName();
    }

    /**
     * Retrieves names of all tables that form the cycle, in the order of the cycle.
     *
     * @return list of table names
     */
    public List<String> getTableNames() {
        return foreignKeys.stream()
            .map(ForeignKey::getTableName)
            .toList();
    }

    /**
     * Retrieves foreign keys that form the cycle, in the order of the cycle.
     *
     * @return list of foreign keys
     * @see ForeignKey
     */
    public List<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ConstraintNameAware> getConstraints() {
        return List.copyOf(getForeignKeys());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final ForeignKeyCycle that)) {
            return false;
        }

        return Objects.equals(foreignKeys, that.foreignKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(foreignKeys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final ForeignKeyCycle other) {
        Objects.requireNonNull(other, "other cannot be null");
        final int commonSize = Math.min(foreignKeys.size(), other.foreignKeys.size());
        for (int i = 0; i < commonSize; ++i) {
            final int result = foreignKeys.get(i).compareTo(other.foreignKeys.get(i));
            if (result != 0) {
                return result;
            }
        }
        return Integer.compare(foreignKeys.size(), other.foreignKeys.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ForeignKeyCycle.class.getSimpleName() + '{' +
            FOREIGN_KEYS_FIELD + '=' + foreignKeys +
            '}';
    }

    /**
     * Constructs a {@code ForeignKeyCycle} object from a given list of foreign keys.
     *
     * @param foreignKeys foreign keys ordered along the cycle; should be non-null, contain at least two items and no repeated tables.
     * @return {@code ForeignKeyCycle}
     */
    public static ForeignKeyCycle of(final Collection<ForeignKey> foreignKeys) {
        return new ForeignKeyCycle(foreignKeys);
    }

    /**
     * Constructs a {@code ForeignKeyCycle} object from given foreign keys.
     *
     * @param firstForeignKey  first foreign key; should be non-null.
     * @param secondForeignKey second foreign key; should be non-null.
     * @param otherForeignKeys other foreign keys.
     * @return {@code ForeignKeyCycle}
     */
    public static ForeignKeyCycle of(final ForeignKey firstForeignKey,
                                     final ForeignKey secondForeignKey,
                                     final ForeignKey... otherForeignKeys) {
        return new ForeignKeyCycle(DuplicatedIndexesParser.combine(firstForeignKey, secondForeignKey, otherForeignKeys));
    }
}
