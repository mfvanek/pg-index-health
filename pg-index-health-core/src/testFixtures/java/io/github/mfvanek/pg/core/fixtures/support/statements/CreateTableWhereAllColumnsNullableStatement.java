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

public class CreateTableWhereAllColumnsNullableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.bad_design (
                    product_type text not null,
                    description text,
                    product_subtype text not null,
                    primary key (product_type, product_subtype)
                );""",
            """
                create table if not exists {schemaName}."no-pk" (
                    description text,
                    product_type text,
                    product_subtype text
                );""",
            """
                create table if not exists {schemaName}.only_pk (
                    id bigint generated always as identity primary key
                );""",
            """
                create table if not exists {schemaName}."good-design" (
                    description text not null,
                    id bigint generated always as identity primary key
                );"""
        );
    }
}
