/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support.statements;

import java.util.List;
import javax.annotation.Nonnull;

public class CreateProceduresStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute(@Nonnull final String schemaName) {
        return List.of(
            String.format("create or replace procedure %1$s.insert_data(a integer, b integer) " +
                " language sql " +
                "as $$ " +
                "insert into %1$s.clients values (a); " +
                "insert into %1$s.clients values (b); " +
                "$$;", schemaName),
            String.format("create or replace procedure %1$s.insert_data(a int, b int, c int) " +
                "language sql " +
                "as $$ " +
                "insert into %1$s.clients values (a); " +
                "insert into %1$s.clients values (b); " +
                "insert into %1$s.clients values (c); " +
                "$$;", schemaName)
        );
    }
}
