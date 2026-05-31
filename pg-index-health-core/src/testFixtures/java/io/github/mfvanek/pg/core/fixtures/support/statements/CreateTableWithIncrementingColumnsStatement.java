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

public class CreateTableWithIncrementingColumnsStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}.orders (
                    id         bigint generated always as identity primary key,
                    phone1     text,
                    phone2     text,
                    address1   text,
                    address2   text,
                    address3   text,
                    created_at timestamptz,
                    created_by text,
                    sku1       text,
                    "updatedAt" timestamptz,
                    "updatedBy" text
                );"""
        );
    }
}
