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

public class CreateDictTableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.dict(
                    ref_type int not null primary key,
                    ref_value varchar(64),
                    description text
                );""",
            "create unique index if not exists idx_dict_ref_type_ref_value on {schemaName}.dict (ref_type, ref_value);",
            "comment on table {schemaName}.dict is 'Dictionary table for testing composite foreign keys';",
            "comment on column {schemaName}.dict.ref_type is 'Id of dictionary record';",
            "comment on column {schemaName}.dict.ref_value is 'Value of dictionary record';",
            "comment on column {schemaName}.dict.description is 'Description of dictionary record';",
            "insert into {schemaName}.dict (ref_type, ref_value, description) values (10, '10', 'First value');",
            "insert into {schemaName}.dict (ref_type, ref_value, description) values (20, '20', 'Second value');"
        );
    }
}
