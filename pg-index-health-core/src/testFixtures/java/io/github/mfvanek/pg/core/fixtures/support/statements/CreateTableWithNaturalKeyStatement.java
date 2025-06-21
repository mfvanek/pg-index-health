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

public class CreateTableWithNaturalKeyStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.good (
                    id int not null primary key);""",
            """
                create table if not exists {schemaName}."times-of-creation" (
                    "time-of-creation" timestamptz not null primary key);""",
            """
                create table if not exists {schemaName}.t2_composite (
                    passport_series text not null,
                    passport_number text not null,
                    primary key (passport_series, passport_number)
                );""",
            """
                create table if not exists {schemaName}.t3_composite (
                    app_id uuid not null,
                    app_number text not null,
                    primary key (app_id, app_number)
                );"""
        );
    }
}
