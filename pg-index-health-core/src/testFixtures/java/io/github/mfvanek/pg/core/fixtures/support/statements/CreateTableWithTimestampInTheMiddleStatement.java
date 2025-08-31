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

public class CreateTableWithTimestampInTheMiddleStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}."t-multi" (
                    id int primary key,
                    ts timestamp,
                    created_at timetz,
                    name text
                );""",
            "create index idx_multi_mid on {schemaName}.\"t-multi\" (id, ts, name);",
            "create index idx_multi_end on {schemaName}.\"t-multi\" (id, name, ts);",
            "create index idx_multi_none on {schemaName}.\"t-multi\" (id, name);",
            "create index idx_multi_expr_mid on {schemaName}.\"t-multi\" (id, date_trunc('day', ts), name);",
            "create index idx_multi_expr_first on {schemaName}.\"t-multi\" (date_trunc('day', ts), id, name);",
            "create unique index idx_unique_ts on {schemaName}.\"t-multi\" (id, ts, id);"
        );
    }
}
