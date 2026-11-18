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

/**
 * Represents a database statement that creates a partitioned table with nullable fields
 * and a partitioned table without a primary key whose default partition has a primary key.
 */
public class CreatePartitionedTableWithNullableFieldsStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreatePartitionedTableWithNullableFieldsStatement}.
     */
    public CreatePartitionedTableWithNullableFieldsStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.custom_entity_reference_with_very_very_very_long_name(
                    ref_type varchar(32),
                    ref_value varchar(64),
                    creation_date timestamp with time zone not null,
                    entity_id varchar(64) not null
                ) partition by range (creation_date);""",
            """
                create index if not exists idx_custom_entity_reference_with_very_very_very_long_name_1
                    on {schemaName}.custom_entity_reference_with_very_very_very_long_name (ref_type, ref_value);""",
            """
                create index if not exists idx_custom_entity_reference_with_very_very_very_long_name_2
                    on {schemaName}.custom_entity_reference_with_very_very_very_long_name (entity_id, ref_value);""",
            """
                create table if not exists {schemaName}.custom_entity_reference_with_very_very_very_long_name_1_default
                    partition of {schemaName}.custom_entity_reference_with_very_very_very_long_name default;""",
            """
                create index if not exists idx_custom_entity_reference_with_very_very_very_long_name_1_d_3
                    on {schemaName}.custom_entity_reference_with_very_very_very_long_name_1_default (ref_type, ref_value);""",
            // Partitioned table without a primary key whose default partition has its own primary key
            """
                create table if not exists {schemaName}.pk_only_in_partition(
                    id bigint not null,
                    creation_date timestamp with time zone not null
                ) partition by range (creation_date);""",
            """
                create table if not exists {schemaName}.pk_only_in_partition_default
                    partition of {schemaName}.pk_only_in_partition default;""",
            "alter table if exists {schemaName}.pk_only_in_partition_default add primary key (id, creation_date);"
        );
    }
}
