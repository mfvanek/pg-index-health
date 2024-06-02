/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support.statements;

import java.util.List;

public class CreateIndexesWithDifferentOpclassStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create index if not exists i_clients_last_name " +
                "on {schemaName}.clients using btree(lower(last_name))",
            "create index if not exists i_clients_last_name_ops " +
                "on {schemaName}.clients using btree(lower(last_name) text_pattern_ops)"
        );
    }
}
