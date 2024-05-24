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

public class CreateSequenceStatement extends AbstractDbStatement {

    public CreateSequenceStatement(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format(
            "drop sequence if exists %1$s.seq_1; " +
                "create sequence %1$s.seq_1 as smallint increment by 1 maxvalue 100 start 92;", schemaName));

        statement.execute(String.format(
            "drop sequence if exists %1$s.seq_3; " +
                "create sequence %1$s.seq_3 as integer increment by 2 maxvalue 100 start 92;", schemaName));

        statement.execute(String.format(
            "drop sequence if exists %1$s.seq_5; " +
                "create sequence %1$s.seq_5 as bigint increment by 10 maxvalue 100 start 92;", schemaName));

        statement.execute(String.format(
            "drop sequence if exists %1$s.seq_cycle; " +
                "create sequence %1$s.seq_cycle as bigint increment by 10 maxvalue 100 start 92 cycle;", schemaName));
    }
}
