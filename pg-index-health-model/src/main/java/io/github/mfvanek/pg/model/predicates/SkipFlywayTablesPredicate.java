/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A predicate that tests if a given {@link DbObject} is a Flyway-related table.
 * <p>
 * This predicate is specifically designed to skip (return {@code false} for) tables
 * associated with Flyway, such as "flyway_schema_history".
 * These tables are used by Flyway to track database changes and ensure that migrations
 * are applied consistently.
 * </p>
 *
 * @author Ivan Vakhrushev
 * @see Predicate
 * @since 0.13.3
 */
@Immutable
@ThreadSafe
public final class SkipFlywayTablesPredicate extends AbstractSkipTablesPredicate {

    /**
     * The set of raw Flyway table names.
     *
     * @see <a href="https://www.red-gate.com/hub/product-learning/flyway/exploring-the-flyway-schema-history-table">flyway_schema_history documentation</a>
     */
    private static final Set<String> RAW_FLYWAY_TABLES = Set.of("flyway_schema_history");

    private SkipFlywayTablesPredicate(@Nonnull final PgContext pgContext) {
        super(pgContext, RAW_FLYWAY_TABLES);
    }

    /**
     * Returns a predicate that skips Flyway tables in the default "public" schema context.
     * <p>
     * This method creates an instance of {@link SkipFlywayTablesPredicate} using a
     * {@link PgContext} that represents the "public" schema, making it easy to filter
     * Flyway tables in environments where the default schema is used.
     * </p>
     *
     * @return a predicate that skips Flyway tables in the "public" schema
     */
    public static Predicate<DbObject> ofPublic() {
        return new SkipFlywayTablesPredicate(PgContext.ofPublic());
    }

    /**
     * Returns a predicate that skips Flyway tables in the specified schema context.
     * <p>
     * This method creates an instance of {@link SkipFlywayTablesPredicate} using the
     * provided {@link PgContext} to enrich Flyway table names with a specific schema,
     * allowing for filtering in environments where a schema other than "public" is used.
     * </p>
     *
     * @param pgContext the schema context to enrich Flyway table names
     * @return a predicate that skips Flyway tables in the specified schema
     */
    public static Predicate<DbObject> of(@Nonnull final PgContext pgContext) {
        return new SkipFlywayTablesPredicate(pgContext);
    }
}
