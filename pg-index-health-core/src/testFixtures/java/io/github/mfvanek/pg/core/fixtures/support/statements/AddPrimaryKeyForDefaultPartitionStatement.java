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

public class AddPrimaryKeyForDefaultPartitionStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of("""
            alter table if exists {schemaName}.custom_entity_reference_with_very_very_very_long_name_1_default
                add primary key (ref_type, ref_value, creation_date, entity_id);"""
        );
    }
}
