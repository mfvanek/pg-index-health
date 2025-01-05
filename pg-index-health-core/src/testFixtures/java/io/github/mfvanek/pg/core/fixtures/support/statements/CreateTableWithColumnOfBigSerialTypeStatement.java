/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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

public class CreateTableWithColumnOfBigSerialTypeStatement extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of("create table if not exists {schemaName}.bad_accounts (" +
            "id bigserial not null primary key, " +
            "name varchar(255) not null," +
            // not null constraint will be added for all serial columns
            "real_client_id bigserial," +
            "real_account_id bigserial)");
    }

    @Override
    public void postExecute(@Nonnull final Statement statement, @Nonnull final String schemaName) throws SQLException {
        throwExceptionIfTableDoesNotExist(statement, "bad_accounts", schemaName);
    }
}
