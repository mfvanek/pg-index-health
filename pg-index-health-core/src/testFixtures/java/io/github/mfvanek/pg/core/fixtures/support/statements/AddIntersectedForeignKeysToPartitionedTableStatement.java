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

public class AddIntersectedForeignKeysToPartitionedTableStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "alter table if exists {schemaName}.dict add column ref_value varchar(64);",
            "create unique index if not exists idx_dict_ref_type_ref_value on {schemaName}.dict (ref_type, ref_value);",
            """
                alter table if exists {schemaName}.t1
                    add constraint t1_ref_type_ref_value_fk \
                    foreign key (ref_type, ref_value) references {schemaName}.dict(ref_type, ref_value);"""
        );
    }
}
