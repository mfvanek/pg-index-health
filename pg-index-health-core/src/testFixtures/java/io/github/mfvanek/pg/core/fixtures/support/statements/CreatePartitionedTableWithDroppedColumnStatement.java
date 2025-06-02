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

public class CreatePartitionedTableWithDroppedColumnStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create table if not exists {schemaName}.tp(" +
                "ref_type bigint not null," +
                "entity_id varchar(64) not null" +
                ") partition by range (ref_type);",
            "alter table if exists {schemaName}.tp drop column entity_id;",
            "create table if not exists {schemaName}.tp_default " +
                "partition of {schemaName}.tp default;"
        );
    }
}
