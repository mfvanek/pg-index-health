/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import java.util.List;

public class CreatePartitionedTableWithVeryLongNamesStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create table if not exists {schemaName}.entity_long_1234567890_1234567890_1234567890_1234567890_1234567(" +
                "ref_type varchar(32)," +
                "ref_value varchar(64)," +
                "entity_id bigserial primary key" +
                ") partition by range (entity_id);",
            "create index if not exists idx_entity_long_1234567890_1234567890_1234567890_1234567890_123 " +
                "on {schemaName}.entity_long_1234567890_1234567890_1234567890_1234567890_1234567 (ref_type, ref_value);",
            "create table if not exists {schemaName}.entity_default_long_1234567890_1234567890_1234567890_1234567890 " +
                "partition of {schemaName}.entity_long_1234567890_1234567890_1234567890_1234567890_1234567 default;"
        );
    }
}
