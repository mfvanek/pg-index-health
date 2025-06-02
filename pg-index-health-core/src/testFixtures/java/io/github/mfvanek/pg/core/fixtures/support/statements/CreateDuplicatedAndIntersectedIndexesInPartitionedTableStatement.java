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

public class CreateDuplicatedAndIntersectedIndexesInPartitionedTableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create index if not exists idx_t1_deleted_duplicate on {schemaName}.t1(deleted);",
            "create index if not exists idx_t1_deleted_entity_id on {schemaName}.t1(deleted, entity_id);"
        );
    }
}
