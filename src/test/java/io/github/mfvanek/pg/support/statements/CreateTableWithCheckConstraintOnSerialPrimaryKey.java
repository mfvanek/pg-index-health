/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
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

public class CreateTableWithCheckConstraintOnSerialPrimaryKey extends AbstractDbStatement {

    public CreateTableWithCheckConstraintOnSerialPrimaryKey(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("create table if not exists %1$s.another_table(" +
                        "id bigserial primary key, " +
                        "constraint not_reserved_id check (id > 1000), " +
                        "constraint less_than_million check (id < 1000000));",
                schemaName));
    }
}
