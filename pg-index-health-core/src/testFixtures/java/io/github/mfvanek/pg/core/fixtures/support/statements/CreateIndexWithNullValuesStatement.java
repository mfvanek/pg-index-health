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

public class CreateIndexWithNullValuesStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of("create index if not exists i_clients_middle_name on {schemaName}.clients (middle_name)");
    }
}
