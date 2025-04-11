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

public class CreateBadlyNamedPartitionedTableStatement extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create table if not exists {schemaName}.\"one-partitioned\"(" +
                "\"bad-id\" bigserial not null primary key" +
                ") partition by range (\"bad-id\");",
            "create table if not exists {schemaName}.\"one-default\" " +
                "partition of {schemaName}.\"one-partitioned\" default;"
        );
    }
}
