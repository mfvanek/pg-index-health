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

public class CreateClientsTableStatement extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create sequence if not exists {schemaName}.clients_seq",
            "create table if not exists {schemaName}.clients (" +
                "id bigint not null primary key default nextval('{schemaName}.clients_seq')," +
                "last_name varchar(255) not null," +
                "first_name varchar(255) not null," +
                "middle_name varchar(255)," +
                "info jsonb)"
        );
    }
}
