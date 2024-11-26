/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.annotation.Nonnull;

public class CreateTableWithoutPrimaryKeyStatement extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of("create table if not exists {schemaName}.bad_clients (" +
            "id bigint not null, " +
            "name varchar(255) not null," +
            "real_client_id integer," +
            "email varchar(200)," +
            "phone varchar(51))");
    }

    @Override
    public void postExecute(@Nonnull final Statement statement, @Nonnull final String schemaName) throws SQLException {
        throwExceptionIfTableDoesNotExist(statement, "bad_clients", schemaName);
    }
}
