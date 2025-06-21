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

public class CreatePartitionedIndexWithUnnecessaryWhereClauseStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.one_partitioned(
                    "first-ref" bigint not null,
                    second_ref  bigint not null
                ) partition by range (second_ref);""",
            """
                create index if not exists "idx_second_ref_first-ref"
                    on {schemaName}.one_partitioned (second_ref, "first-ref")
                    where "first-ref" is not null;""",
            "create table if not exists {schemaName}.one_default partition of {schemaName}.one_partitioned default;"
        );
    }
}
