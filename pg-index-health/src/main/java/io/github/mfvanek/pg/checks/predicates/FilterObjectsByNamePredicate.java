/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.predicates;

import io.github.mfvanek.pg.model.DbObject;

import java.util.Collection;
import java.util.Locale;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Allows filter database objects by their name.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.2
 * @deprecated This class has been replaced by {@link io.github.mfvanek.pg.model.predicates.SkipDbObjectsByNamePredicate}
 */
@Deprecated(since = "0.13.3", forRemoval = true)
public class FilterObjectsByNamePredicate extends AbstractFilterByName implements Predicate<DbObject> {

    private FilterObjectsByNamePredicate(@Nonnull final Collection<String> exclusions) {
        super(exclusions);
    }

    private FilterObjectsByNamePredicate(@Nonnull final String objectName) {
        super(objectName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test(@Nonnull final DbObject objectNameAware) {
        if (exclusions.isEmpty()) {
            return true;
        }
        return !exclusions.contains(objectNameAware.getName().toLowerCase(Locale.ROOT));
    }

    /**
     * Factory method for creating a {@code FilterObjectsByNamePredicate} with a single object name.
     *
     * @param objectName the name of the object to be excluded
     * @return a new {@code Predicate<DbObject>} for the specified object name
     */
    @Nonnull
    public static Predicate<DbObject> of(@Nonnull final String objectName) {
        return new FilterObjectsByNamePredicate(objectName);
    }

    /**
     * Factory method for creating a {@code FilterObjectsByNamePredicate} with a collection of object names.
     *
     * @param exclusions a collection of object names to be excluded
     * @return a new {@code Predicate<DbObject>} for the specified collection of object names
     */
    @Nonnull
    public static Predicate<DbObject> of(@Nonnull final Collection<String> exclusions) {
        return new FilterObjectsByNamePredicate(exclusions);
    }
}
