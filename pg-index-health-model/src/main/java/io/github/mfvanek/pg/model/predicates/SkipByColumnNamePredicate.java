/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.column.ColumnNameAware;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A predicate that filters out database objects based on a specified set of column names.
 *
 * @author Ivan Vakhrushev
 * @see Predicate
 * @see ColumnNameAware
 * @see ColumnsAware
 * @since 0.15.0
 */
public final class SkipByColumnNamePredicate implements Predicate<DbObject> {

    private final Set<String> columnNamesToSkip;

    private SkipByColumnNamePredicate(final Collection<String> columnNamesToSkip) {
        this.columnNamesToSkip = AbstractSkipTablesPredicate.prepareNamesToSkip(columnNamesToSkip);
    }

    private SkipByColumnNamePredicate(final String columnNameToSkip) {
        this(AbstractSkipTablesPredicate.prepareSingleNameToSkip(columnNameToSkip, "columnNameToSkip"));
    }

    /**
     * Tests whether the given database object should be skipped based on its column name.
     *
     * @param dbObject the database object to test; must be non-null
     * @return {@code false} if the object's column name matches one in the skip list; {@code true} otherwise
     */
    @Override
    public boolean test(final DbObject dbObject) {
        if (columnNamesToSkip.isEmpty()) {
            return true;
        }
        if (dbObject instanceof final ColumnNameAware c) {
            return !columnNamesToSkip.contains(c.getColumnName().toLowerCase(Locale.ROOT));
        }
        if (dbObject instanceof final ColumnsAware cs) {
            for (final ColumnNameAware c : cs.getColumns()) {
                if (columnNamesToSkip.contains(c.getColumnName().toLowerCase(Locale.ROOT))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates a predicate to skip a specific column name.
     *
     * @param columnNameToSkip the column name to skip; must be non-blank
     * @return a {@code SkipByColumnNamePredicate} instance for the specified column name
     */
    public static Predicate<DbObject> ofName(final String columnNameToSkip) {
        return new SkipByColumnNamePredicate(columnNameToSkip);
    }

    /**
     * Creates a predicate to skip a collection of column names.
     *
     * @param columnNamesToSkip the collection of column names to skip; must be non-null
     * @return a {@code SkipByColumnNamePredicate} instance for the specified column names
     */
    public static Predicate<DbObject> of(final Collection<String> columnNamesToSkip) {
        return new SkipByColumnNamePredicate(columnNamesToSkip);
    }
}
