/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support.statements;

import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Nonnull;

public class CreateIndexesWithDifferentOpclassStatement extends AbstractDbStatement {

    public CreateIndexesWithDifferentOpclassStatement(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("create index if not exists i_clients_last_name " +
                "on %s.clients using btree(lower(last_name))", schemaName));
        statement.execute(String.format("create index if not exists i_clients_last_name_ops " +
                "on %s.clients using btree(lower(last_name) text_pattern_ops)", schemaName));
    }
}
