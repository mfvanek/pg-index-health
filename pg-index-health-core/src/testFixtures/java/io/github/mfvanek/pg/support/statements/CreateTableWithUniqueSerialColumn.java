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
import javax.annotation.Nonnull;

public class CreateTableWithUniqueSerialColumn extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create table if not exists {schemaName}.one_more_table(" +
                "id bigserial, " +
                "constraint unique_id unique (id), " +
                "constraint not_reserved_id check (id > 1000), " +
                "constraint less_than_million check (id < 1000000));"
        );
    }
}
