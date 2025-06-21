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

public class CreateTableWithSerialPrimaryKeyReferencesToAnotherTableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.test_table(
                    id bigserial,
                    num bigserial,
                    constraint test_table_pkey_id primary key (id),
                    constraint test_table_fkey_other_id foreign key (id) references {schemaName}.another_table (id),
                    constraint test_table_fkey_one_more_id foreign key (id) references {schemaName}.one_more_table (id)
                );"""
        );
    }
}
