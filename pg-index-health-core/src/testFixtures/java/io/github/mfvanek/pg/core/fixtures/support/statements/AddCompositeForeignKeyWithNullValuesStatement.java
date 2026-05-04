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

public class AddCompositeForeignKeyWithNullValuesStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table {schemaName}.referencing_bad_table (
                    rbt_id integer not null,
                    rbt_value text, /* nullable */
                    constraint "referencing-bad-table-fk" foreign key (rbt_id, rbt_value)
                        references {schemaName}.dict (ref_type, ref_value)
                );""",
            "insert into {schemaName}.referencing_bad_table (rbt_id, rbt_value) values (20, '20');",
            "insert into {schemaName}.referencing_bad_table (rbt_id, rbt_value) values (30, null);",
            """
                create table {schemaName}.referencing_good_table (
                    rgt_id integer not null,
                    rgt_value text, -- nullable
                    constraint referencing_good_table_fk foreign key (rgt_id, rgt_value)
                        references {schemaName}.dict (ref_type, ref_value) match full
                );""",
            "insert into {schemaName}.referencing_good_table (rgt_id, rgt_value) values (20, '20');"
        );
    }
}
