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

public class CreateForeignKeyOnNullableColumnStatement extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of("alter table if exists {schemaName}.bad_clients " +
            "add constraint c_bad_clients_fk_real_client_id foreign key (real_client_id) references {schemaName}.clients (id);");
    }
}
