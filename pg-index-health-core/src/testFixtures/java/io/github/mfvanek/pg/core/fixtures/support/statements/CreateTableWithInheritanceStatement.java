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

public class CreateTableWithInheritanceStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table {schemaName}.parent_table(
                    id bigint generated always as identity primary key,
                    info text
                );""",
            """
                create table {schemaName}.child_table(
                    extra_info text
                ) inherits ({schemaName}.parent_table);""",
            """
                create table {schemaName}."second-child_table"(
                    extra_info2 text
                ) inherits ({schemaName}.child_table);
                """
        );
    }
}
