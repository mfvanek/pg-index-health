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

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
public final class SkipLiquibaseTablesPredicate implements Predicate<DbObject> {

    /**
     * The list of raw Liquibase table names.
     *
     * @see <a href="https://docs.liquibase.com/concepts/tracking-tables/databasechangelog-table.html">databasechangelog documentation</a>
     * @see <a href="https://docs.liquibase.com/concepts/tracking-tables/databasechangeloglock-table.html">databasechangeloglock documentation</a>
     */
    private static final List<String> RAW_LIQUIBASE_TABLES = List.of("databasechangelog", "databasechangeloglock");

    /**
     * The list of fully qualified Liquibase table names enriched with schema from {@link PgContext}.
     */
    private final List<String> liquibaseTables;

    private SkipLiquibaseTablesPredicate(@Nonnull final PgContext pgContext) {
        Objects.requireNonNull(pgContext, "pgContext cannot be null");
        this.liquibaseTables = RAW_LIQUIBASE_TABLES.stream()
            .map(pgContext::enrichWithSchema)
            .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Evaluates this predicate on the given {@code DbObject}.
     * <p>
     * Returns {@code false} if the {@code DbObject} is a {@link TableNameAware} instance
     * with a table name matching any of the Liquibase table names in the enriched schema.
     * Otherwise, returns {@code true}.
     * </p>
     *
     * @param dbObject the object to be tested
     * @return {@code false} if the {@code DbObject} is a Liquibase-related table, {@code true} otherwise
     */
    @Override
    public boolean test(@Nonnull final DbObject dbObject) {
        if (dbObject instanceof TableNameAware) {
            final TableNameAware t = (TableNameAware) dbObject;
            for (final String liquibaseTable : liquibaseTables) {
                if (t.getTableName().equalsIgnoreCase(liquibaseTable)) {
                    return false;
                }
            }
        }
        return true;
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
