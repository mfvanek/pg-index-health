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

import io.github.mfvanek.pg.model.object.DbObject;
import io.github.mfvanek.pg.model.context.PgContext;

import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A predicate that tests if a given {@link DbObject} is a Liquibase-related table.
 * <p>
 * This predicate is specifically designed to skip (return {@code false} for) tables
 * associated with Liquibase, such as "databasechangelog" and "databasechangeloglock".
 * These tables are used by Liquibase to track database changes and ensure that migrations
 * are applied consistently.
 * </p>
 *
 * @author Ivan Vakhrushev
 * @see Predicate
 * @since 0.13.3
 */
@Immutable
@ThreadSafe
public final class SkipLiquibaseTablesPredicate extends AbstractSkipTablesPredicate {

    /**
     * The set of raw Liquibase table names.
     *
     * @see <a href="https://docs.liquibase.com/concepts/tracking-tables/databasechangelog-table.html">databasechangelog documentation</a>
     * @see <a href="https://docs.liquibase.com/concepts/tracking-tables/databasechangeloglock-table.html">databasechangeloglock documentation</a>
     */
    private static final Set<String> RAW_LIQUIBASE_TABLES = Set.of("databasechangelog", "databasechangeloglock");

    private SkipLiquibaseTablesPredicate(@Nonnull final PgContext pgContext) {
        super(pgContext, RAW_LIQUIBASE_TABLES);
    }

    /**
     * Returns a predicate that skips Liquibase tables in the default "public" schema context.
     * <p>
     * This method creates an instance of {@link SkipLiquibaseTablesPredicate} using a
     * {@link PgContext} that represents the "public" schema, making it easy to filter
     * Liquibase tables in environments where the default schema is used.
     * </p>
     *
     * @return a predicate that skips Liquibase tables in the "public" schema
     */
    public static Predicate<DbObject> ofPublic() {
        return new SkipLiquibaseTablesPredicate(PgContext.ofPublic());
    }

    /**
     * Returns a predicate that skips Liquibase tables in the specified schema context.
     * <p>
     * This method creates an instance of {@link SkipLiquibaseTablesPredicate} using the
     * provided {@link PgContext} to enrich Liquibase table names with a specific schema,
     * allowing for filtering in environments where a schema other than "public" is used.
     * </p>
     *
     * @param pgContext the schema context to enrich Liquibase table names
     * @return a predicate that skips Liquibase tables in the specified schema
     */
    public static Predicate<DbObject> of(@Nonnull final PgContext pgContext) {
        return new SkipLiquibaseTablesPredicate(pgContext);
    }
}
