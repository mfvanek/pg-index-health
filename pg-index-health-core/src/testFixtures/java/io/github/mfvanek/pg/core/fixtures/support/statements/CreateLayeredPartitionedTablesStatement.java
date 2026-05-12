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

public class CreateLayeredPartitionedTablesStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            // Two-level empty partitioned table: all leaf partitions have no data — should be detected
            """
                create table if not exists {schemaName}.two_level_partitioned_empty(
                    id     bigint,
                    region text,
                    val    text
                ) partition by range (id);""",
            """
                create table if not exists {schemaName}.two_level_partitioned_empty_p1
                    partition of {schemaName}.two_level_partitioned_empty
                    for values from (1) to (1000) partition by list (region);""",
            """
                create table if not exists {schemaName}.two_level_partitioned_empty_p1_us
                    partition of {schemaName}.two_level_partitioned_empty_p1 for values in ('us');""",
            """
                create table if not exists {schemaName}.two_level_partitioned_empty_p1_eu
                    partition of {schemaName}.two_level_partitioned_empty_p1 for values in ('eu');""",
            // Two-level partitioned table with data in a leaf partition — should NOT be detected
            """
                create table if not exists {schemaName}.two_level_partitioned_with_data(
                    id     bigint,
                    region text,
                    val    text
                ) partition by range (id);""",
            """
                create table if not exists {schemaName}.two_level_partitioned_with_data_p1
                    partition of {schemaName}.two_level_partitioned_with_data
                    for values from (1) to (1000) partition by list (region);""",
            """
                create table if not exists {schemaName}.two_level_partitioned_with_data_p1_us
                    partition of {schemaName}.two_level_partitioned_with_data_p1 for values in ('us');""",
            """
                create table if not exists {schemaName}.two_level_partitioned_with_data_p1_eu
                    partition of {schemaName}.two_level_partitioned_with_data_p1 for values in ('eu');""",
            "insert into {schemaName}.two_level_partitioned_with_data (id, region, val) values (50, 'us', 'hello');"
        );
    }
}
