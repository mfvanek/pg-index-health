/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import java.util.List;

public class CreateBadlyNamedObjectsStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}."bad-table"(
                    "bad-id" serial not null primary key
                );""",
            """
                create table if not exists {schemaName}."bad-table-two"(
                    "bad-ref-id" int not null primary key,
                    description text
                );""",
            """
                alter table if exists {schemaName}."bad-table-two"
                    add constraint "bad-table-two-fk-bad-ref-id"
                    foreign key ("bad-ref-id") references {schemaName}."bad-table" ("bad-id") not valid;""",
            """
                create or replace function {schemaName}."bad-add"(a integer, b integer) returns integer
                    as 'select $1 + $2;'
                    language sql
                    immutable
                    returns null on null input;"""
        );
    }
}
