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

import java.util.List;

public class CreateSequenceStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "drop sequence if exists {schemaName}.seq_1; " +
                "create sequence {schemaName}.seq_1 as smallint increment by 1 maxvalue 100 start 92;",
            "drop sequence if exists {schemaName}.seq_3; " +
                "create sequence {schemaName}.seq_3 as integer increment by 2 maxvalue 100 start 92;",
            "drop sequence if exists {schemaName}.seq_5; " +
                "create sequence {schemaName}.seq_5 as bigint increment by 10 maxvalue 100 start 92;",
            "drop sequence if exists {schemaName}.seq_cycle; " +
                "create sequence {schemaName}.seq_cycle as bigint increment by 10 maxvalue 100 start 92 cycle;"
        );
    }
}
