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

public class AddNotValidConstraintToPartitionedTableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                alter table if exists {schemaName}.t1
                add constraint t1_entity_id_not_validated_yet
                check (entity_id != '') not valid;""",
            """
                alter table if exists {schemaName}.t1_default
                add constraint t1_default_entity_id_not_validated_yet
                check (entity_id != '') not valid;"""
        );
    }
}
