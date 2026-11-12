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
 * Represents a database statement that creates a partitioned table with non-standard naming.
 */
public class CreateBadlyNamedPartitionedTableStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateBadlyNamedPartitionedTableStatement}.
     */
    public CreateBadlyNamedPartitionedTableStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}."one-partitioned"(
                    "bad-id" bigserial not null primary key,
                    description text
                ) partition by range ("bad-id");""",
            """
                create table if not exists {schemaName}."one-default"
                    partition of {schemaName}."one-partitioned" default;""",
            """
                alter table if exists {schemaName}."one-default"
                add constraint "one-default_description_not_null" check (description is not null);"""
        );
    }
}
