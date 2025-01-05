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

public class AddCommentOnColumnsStatement extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "comment on column {schemaName}.clients.id is 'Unique record ID';",
            "comment on column {schemaName}.clients.last_name is 'Customer''s last name';",
            "comment on column {schemaName}.clients.first_name is 'Customer''s given name';",
            "comment on column {schemaName}.clients.middle_name is 'Patronymic of the customer';",
            "comment on column {schemaName}.clients.info is 'Raw client data';",
            "comment on column {schemaName}.accounts.id is 'Unique record ID';",
            "comment on column {schemaName}.accounts.client_id is 'Customer record ID';",
            "comment on column {schemaName}.accounts.account_number is 'Customer''s account number';",
            "comment on column {schemaName}.accounts.account_balance is 'The balance on the customer''s account';",
            "comment on column {schemaName}.accounts.deleted is 'Indicates that the account has been deleted';"
        );
    }
}
