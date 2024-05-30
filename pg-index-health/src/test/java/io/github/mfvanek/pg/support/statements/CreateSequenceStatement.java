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

public class CreateSequenceStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute(@Nonnull final String schemaName) {
        return List.of(
            String.format(
                "drop sequence if exists %1$s.seq_1; " +
                    "create sequence %1$s.seq_1 as smallint increment by 1 maxvalue 100 start 92;", schemaName),
            String.format(
                "drop sequence if exists %1$s.seq_3; " +
                    "create sequence %1$s.seq_3 as integer increment by 2 maxvalue 100 start 92;", schemaName),
            String.format(
                "drop sequence if exists %1$s.seq_5; " +
                    "create sequence %1$s.seq_5 as bigint increment by 10 maxvalue 100 start 92;", schemaName),
            String.format(
                "drop sequence if exists %1$s.seq_cycle; " +
                    "create sequence %1$s.seq_cycle as bigint increment by 10 maxvalue 100 start 92 cycle;", schemaName)
        );
    }
}
