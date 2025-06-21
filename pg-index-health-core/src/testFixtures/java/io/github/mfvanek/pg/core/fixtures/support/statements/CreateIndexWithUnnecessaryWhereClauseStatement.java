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

public class CreateIndexWithUnnecessaryWhereClauseStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.t1(
                    id bigint not null primary key,
                    id_ref bigint not null
                );""",
            """
                create index if not exists idx_t1_id_ref on {schemaName}.t1 (id_ref)
                    where id_ref is not null;""",
            """
                create table if not exists {schemaName}.t2(
                    "first-ref" bigint not null,
                    second_ref bigint not null,
                    t1_id bigint references {schemaName}.t1 (id)
                );""",
            """
                create index if not exists "idx_t2_first-ref_second_ref" on {schemaName}.t2 (second_ref, "first-ref")
                    where "first-ref" is not null;""",
            """
                create index if not exists idx_t2_id_ref on {schemaName}.t2 (t1_id)
                    where t1_id is not null;""",
            """
                create index if not exists idx_second_ref_t1_id on {schemaName}.t2 (t1_id, second_ref)
                    where t1_id is not null;"""
        );
    }
}
