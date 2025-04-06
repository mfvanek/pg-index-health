/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.checks.extractors.TableWithColumnsExtractor;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.table.TableWithColumns;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for tables with zero or one column on a specific host.
 * This usually indicates a poor design of tables in the database or the presence of garbage.
 *
 * @author Ivan Vahrushev
 * @since 0.14.6
 */
public class TablesWithZeroOrOneColumnCheckOnHost extends AbstractCheckOnHost<TableWithColumns> {

    public TablesWithZeroOrOneColumnCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(TableWithColumns.class, pgConnection, Diagnostic.TABLES_WITH_ZERO_OR_ONE_COLUMN);
    }

    /**
     * Returns tables with zero or one column in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of tables with zero or one column
     */
    @Nonnull
    @Override
    protected List<TableWithColumns> doCheck(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, TableWithColumnsExtractor.of());
    }
}
