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

public class CreatePartitionedTableWithJsonAndSerialColumnsStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.parent(
                    ref_type varchar(32),
                    ref_value varchar(64),
                    creation_date timestamp with time zone not null,
                    entity_id varchar(64) not null,
                    real_client_id bigserial,
                    raw_data json
                ) partition by range (creation_date);""",
            """
                create table if not exists {schemaName}.partition_default
                    partition of {schemaName}.parent default;"""
        );
    }
}
