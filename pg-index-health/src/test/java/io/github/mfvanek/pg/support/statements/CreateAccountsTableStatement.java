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

public class CreateAccountsTableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute(@Nonnull final String schemaName) {
        return List.of(
            String.format("create sequence if not exists %s.accounts_seq", schemaName),
            String.format("create table if not exists %1$s.accounts (" +
                "id bigint not null primary key default nextval('%1$s.accounts_seq')," +
                "client_id bigint not null," +
                "account_number varchar(50) not null unique," +
                "account_balance numeric(22,2) not null default 0," +
                "deleted boolean not null default false)", schemaName)
        );
    }
}
