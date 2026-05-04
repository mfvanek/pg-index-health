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

public class AddCompositeForeignKeyWithNullValuesToPartitionedTableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.referencing_bad_table_partitioned(
                    rbt_id integer not null,
                    rbt_value text, /* nullable */
                    created_at timestamptz not null default current_timestamp,
                    constraint "referencing_bad_table_partitioned-fk" foreign key (rbt_id, rbt_value)
                        references {schemaName}.dict (ref_type, ref_value)
                ) partition by range (created_at);""",
            """
                create table if not exists {schemaName}.referencing_bad_table_default
                    partition of {schemaName}.referencing_bad_table_partitioned default;""",
            "insert into {schemaName}.referencing_bad_table_partitioned (rbt_id, rbt_value) values (20, '20');",
            "insert into {schemaName}.referencing_bad_table_partitioned (rbt_id, rbt_value) values (30, null);"
        );
    }
}
