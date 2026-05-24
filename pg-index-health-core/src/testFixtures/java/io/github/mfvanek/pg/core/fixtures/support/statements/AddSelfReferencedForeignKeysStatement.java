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

public class AddSelfReferencedForeignKeysStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table {schemaName}.bad_self_ref_table (
                    id bigint generated always as identity primary key,
                    parent_id bigint,
                    name text not null,
                    constraint bad_self_ref_table_parent_id_fk
                        foreign key (parent_id) references {schemaName}.bad_self_ref_table (id)
                );""",
            """
                create table {schemaName}.bad_self_ref_restrict_table (
                    id bigint generated always as identity primary key,
                    parent_id bigint,
                    name text not null,
                    constraint bad_self_ref_restrict_table_parent_id_fk
                        foreign key (parent_id) references {schemaName}.bad_self_ref_restrict_table (id)
                        on delete restrict
                );""",
            """
                create table {schemaName}.good_self_ref_table (
                    id bigint generated always as identity primary key,
                    parent_id bigint,
                    name text not null,
                    constraint good_self_ref_table_parent_id_fk
                        foreign key (parent_id) references {schemaName}.good_self_ref_table (id)
                        on delete cascade
                );"""
        );
    }
}
