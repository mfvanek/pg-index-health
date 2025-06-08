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

public class CreateTableWithIdentityPrimaryKeyStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of("""
            create table if not exists {schemaName}.test_table_with_identity_pk(
                id bigint not null generated always as identity,
                num bigserial,
                constraint primary_key_length_62_for_test_table_with_identity_pk_12345678 primary key (id),
                constraint num_less_than_million_constraint_with_length_63_1234567890_1234 check (num < 1000000)
            );"""
        );
    }
}
