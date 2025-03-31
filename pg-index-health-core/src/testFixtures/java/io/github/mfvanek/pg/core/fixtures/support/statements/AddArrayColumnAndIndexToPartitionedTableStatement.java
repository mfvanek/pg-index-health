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
import javax.annotation.Nonnull;

public class AddArrayColumnAndIndexToPartitionedTableStatement extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "alter table if exists {schemaName}.t1 add column if not exists roles text[];",
            "create index if not exists t1_roles_btree_idx on {schemaName}.t1 (roles) where roles is not null;"
        );
    }
}
