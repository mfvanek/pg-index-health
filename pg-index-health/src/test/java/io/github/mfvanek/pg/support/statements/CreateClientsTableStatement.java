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

import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Nonnull;

public class CreateClientsTableStatement extends AbstractDbStatement {

    public CreateClientsTableStatement(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("create sequence if not exists %s.clients_seq", schemaName));
        statement.execute(String.format("create table if not exists %1$s.clients (" +
            "id bigint not null primary key default nextval('%1$s.clients_seq')," +
            "last_name varchar(255) not null," +
            "first_name varchar(255) not null," +
            "middle_name varchar(255)," +
            "info jsonb)", schemaName));
    }
}
