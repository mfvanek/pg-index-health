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

public class CreatePartitionedTableWithoutPrimaryKeyStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.custom_entity_reference_with_very_very_very_long_name(
                    ref_type varchar(32) not null,
                    ref_value varchar(64) not null,
                    creation_date timestamp with time zone not null,
                    entity_id varchar(64) not null
                ) partition by range (creation_date);""",
            """
                create table if not exists {schemaName}.custom_entity_reference_with_very_very_very_long_name_1_default
                    partition of {schemaName}.custom_entity_reference_with_very_very_very_long_name default;"""
        );
    }
}
