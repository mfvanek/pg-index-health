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

public class AddCommentOnTablesStatement extends AbstractDbStatement {

    @Nonnull
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "comment on table {schemaName}.clients is 'Customer Information';",
            "comment on table {schemaName}.accounts is 'Information about customer accounts';"
        );
    }
}
