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

public class CreateClientsTableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create sequence if not exists {schemaName}.clients_seq",
            """
                create table if not exists {schemaName}.clients (
                    id bigint not null primary key default nextval('{schemaName}.clients_seq'),
                    last_name varchar(255) not null,
                    first_name varchar(255) not null,
                    middle_name varchar(255),
                    info jsonb,
                    email varchar(200) not null,
                    phone varchar(50) not null
                )""",
            "create unique index if not exists i_clients_email_phone on {schemaName}.clients (email, phone)",
            "comment on column {schemaName}.clients.email is 'Customer''s email';",
            "comment on column {schemaName}.clients.phone is 'Customer''s phone number';"
        );
    }
}
