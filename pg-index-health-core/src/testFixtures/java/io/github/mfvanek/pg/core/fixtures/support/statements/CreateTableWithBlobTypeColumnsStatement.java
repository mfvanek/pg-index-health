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

public class CreateTableWithBlobTypeColumnsStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create extension if not exists lo schema {schemaName};",
            """
                create table if not exists {schemaName}."document-bad" (
                    id bigserial primary key,
                    title text not null,
                    "content-bad" oid not null
                );""",
            """
                create table if not exists {schemaName}.image (
                    id bigserial primary key,
                    title text not null,
                    raster {schemaName}.lo
                );""",
            """
                create table if not exists {schemaName}.media_file (
                    id bigserial primary key,
                    name text not null,
                    thumbnail oid,
                    full_image {schemaName}.lo not null
                );"""
        );
    }
}
