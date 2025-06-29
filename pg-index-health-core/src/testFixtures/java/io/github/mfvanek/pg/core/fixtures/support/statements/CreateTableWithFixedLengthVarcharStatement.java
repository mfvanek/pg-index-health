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

public class CreateTableWithFixedLengthVarcharStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}."t-varchar-short" (
                    "id-short" varchar(32) not null primary key
                );""",
            "insert into {schemaName}.\"t-varchar-short\" values (replace(gen_random_uuid()::text, '-', ''));",
            """
                create table if not exists {schemaName}.t_varchar_long (
                    id_long varchar(36) not null primary key
                );""",
            "insert into {schemaName}.t_varchar_long values (gen_random_uuid());",
            """
                create table if not exists {schemaName}.t_link (
                    "id-short" varchar(32) not null references {schemaName}."t-varchar-short" ("id-short"),
                    id_long varchar(36) not null references {schemaName}.t_varchar_long (id_long),
                    primary key (id_long, "id-short")
                );""",
            """
                create table if not exists {schemaName}.t_varchar_long_not_pk (
                    id_long varchar(36) not null);""",
            """
                create table if not exists {schemaName}.t_uuid (
                    id uuid not null primary key);"""
        );
    }
}
