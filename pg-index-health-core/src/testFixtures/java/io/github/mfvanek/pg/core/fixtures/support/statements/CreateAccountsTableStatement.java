/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import java.util.List;

public class CreateAccountsTableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create sequence if not exists {schemaName}.accounts_seq",
            """
                create table if not exists {schemaName}.accounts (
                    id bigint not null primary key default nextval('{schemaName}.accounts_seq'),
                    client_id bigint not null,
                    account_number varchar(50) not null unique,
                    account_balance numeric(22,2) not null default 0,
                    deleted boolean not null default false
                )"""
        );
    }
}
