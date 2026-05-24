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

public class AddSelfReferencedForeignKeysToPartitionedTableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.bad_self_ref_partitioned (
                    id bigint not null,
                    parent_id bigint,
                    name text not null,
                    primary key (id),
                    constraint bad_self_ref_partitioned_parent_id_fk
                        foreign key (parent_id) references {schemaName}.bad_self_ref_partitioned (id)
                ) partition by hash (id);""",
            """
                create table if not exists {schemaName}.bad_self_ref_partitioned_0
                    partition of {schemaName}.bad_self_ref_partitioned for values with (modulus 2, remainder 0);""",
            """
                create table if not exists {schemaName}.bad_self_ref_partitioned_1
                    partition of {schemaName}.bad_self_ref_partitioned for values with (modulus 2, remainder 1);"""
        );
    }
}
