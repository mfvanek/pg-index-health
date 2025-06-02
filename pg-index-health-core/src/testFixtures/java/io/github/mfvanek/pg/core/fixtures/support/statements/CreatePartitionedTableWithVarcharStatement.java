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

public class CreatePartitionedTableWithVarcharStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create table if not exists {schemaName}.tp(" +
                "creation_date timestamp not null," +
                "ref_type varchar(36) not null," +
                "entity_id varchar(36) not null," +
                "primary key (creation_date, ref_type, entity_id)" +
                ") partition by range (creation_date);",
            "create table if not exists {schemaName}.tp_default " +
                "partition of {schemaName}.tp default;",
            "create table if not exists {schemaName}.tp_good (" +
                "creation_date timestamp not null," +
                "entity_id uuid not null," +
                "primary key (creation_date, entity_id)" +
                ") partition by range (creation_date);",
            "create table if not exists {schemaName}.\"tp_good-default\" partition of {schemaName}.tp_good default;"
        );
    }
}
