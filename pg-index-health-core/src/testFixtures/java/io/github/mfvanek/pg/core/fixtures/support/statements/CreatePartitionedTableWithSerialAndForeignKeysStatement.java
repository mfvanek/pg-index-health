/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import java.util.List;

public class CreatePartitionedTableWithSerialAndForeignKeysStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.dict(
                    ref_type int not null primary key,
                    description text
                );""",
            """
                create table if not exists {schemaName}.t1(
                    ref_value varchar(64) not null,
                    ref_type bigserial not null references {schemaName}.dict(ref_type),
                    creation_date timestamp with time zone not null,
                    entity_id varchar(64) not null,
                    deleted boolean not null,
                    primary key (ref_value, ref_type, creation_date, entity_id)
                ) partition by range (creation_date);""",
            "create index if not exists idx_t1_deleted on {schemaName}.t1(deleted);",
            """
                create table if not exists {schemaName}.t1_default
                    partition of {schemaName}.t1 default;"""
        );
    }
}
