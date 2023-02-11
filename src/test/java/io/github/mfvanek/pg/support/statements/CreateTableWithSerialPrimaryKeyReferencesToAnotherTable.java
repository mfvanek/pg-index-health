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

public class CreateTableWithSerialPrimaryKeyReferencesToAnotherTable extends AbstractDbStatement {

    public CreateTableWithSerialPrimaryKeyReferencesToAnotherTable(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("create table if not exists %1$s.test_table(" +
                        "id bigserial, " +
                        "num bigserial, " +
                        "constraint test_table_pkey_id primary key (id), " +
                        "constraint test_table_fkey_other_id foreign key (id) references %1$s.another_table (id), " +
                        "constraint test_table_fkey_one_more_id foreign key (id) references %1$s.one_more_table (id));",
                schemaName));
    }
}
