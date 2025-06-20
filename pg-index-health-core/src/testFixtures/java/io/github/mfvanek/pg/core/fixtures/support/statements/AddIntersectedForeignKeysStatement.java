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

public class AddIntersectedForeignKeysStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.client_preferences (
                    id bigint not null generated always as identity,
                    email varchar(200) not null,
                    phone varchar(50) not null,
                    call_time_start timetz not null,
                    call_time_end timetz not null
                )""",
            """
                alter table if exists {schemaName}.client_preferences
                    add constraint c_client_preferences_email_phone_fk
                    foreign key (email, phone) references {schemaName}.clients (email, phone)""",
            """
                alter table if exists {schemaName}.client_preferences
                    add constraint c_client_preferences_phone_email_fk
                    foreign key (phone, email) references {schemaName}.clients (phone, email)"""
        );
    }
}
