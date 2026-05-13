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

public class CreatePartitionedTableWithNoDataStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.partitioned_table_with_no_data(
                    id  bigint,
                    val text
                ) partition by range (id);""",
            """
                create table if not exists {schemaName}.partitioned_table_with_no_data_p1
                    partition of {schemaName}.partitioned_table_with_no_data for values from (1) to (100);""",
            """
                create table if not exists {schemaName}.partitioned_table_with_no_data_p2
                    partition of {schemaName}.partitioned_table_with_no_data for values from (100) to (200);"""
        );
    }
}
