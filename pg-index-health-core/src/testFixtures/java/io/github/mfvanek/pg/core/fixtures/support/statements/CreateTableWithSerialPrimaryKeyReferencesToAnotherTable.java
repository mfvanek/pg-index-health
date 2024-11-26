/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import java.util.List;
import javax.annotation.Nonnull;

public class CreateTableWithSerialPrimaryKeyReferencesToAnotherTable extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create table if not exists {schemaName}.test_table(" +
                "id bigserial, " +
                "num bigserial, " +
                "constraint test_table_pkey_id primary key (id), " +
                "constraint test_table_fkey_other_id foreign key (id) references {schemaName}.another_table (id), " +
                "constraint test_table_fkey_one_more_id foreign key (id) references {schemaName}.one_more_table (id));"
        );
    }
}
