/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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

public class AddInvalidForeignKeyStatement extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "alter table if exists {schemaName}.accounts " +
                "add constraint c_accounts_fk_client_id_not_validated_yet foreign key (client_id) references {schemaName}.clients (id) not valid;",
            "alter table if exists {schemaName}.accounts " +
                "add constraint c_accounts_chk_client_id_not_validated_yet check (client_id > 0) not valid;"
        );
    }
}
