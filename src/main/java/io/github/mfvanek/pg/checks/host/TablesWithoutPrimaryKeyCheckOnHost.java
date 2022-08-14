/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.checks.extractors.TableExtractor;
import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.Table;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for tables without primary key on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.6.0
 */
public class TablesWithoutPrimaryKeyCheckOnHost extends AbstractCheckOnHost<Table> {

    public TablesWithoutPrimaryKeyCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(Table.class, pgConnection, Diagnostic.TABLES_WITHOUT_PRIMARY_KEY);
    }

    /**
     * Returns tables without primary key in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of tables without primary key
     */
    @Nonnull
    @Override
    public List<Table> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, TableExtractor.of());
    }
}
