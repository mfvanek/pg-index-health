/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import java.util.List;

/**
 * Represents a database statement that creates partial indexes for composite foreign keys with predicates checking only a subset of their columns for not null.
 */
public class CreatePartialIndexWithNotNullSubsetForCompositeForeignKeyStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreatePartialIndexWithNotNullSubsetForCompositeForeignKeyStatement}.
     */
    public CreatePartialIndexWithNotNullSubsetForCompositeForeignKeyStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create index if not exists i_referencing_bad_table_not_null on {schemaName}.referencing_bad_table (rbt_id, rbt_value) where rbt_value is not null",
            "create index if not exists i_referencing_good_table_not_null on {schemaName}.referencing_good_table (rgt_id, rgt_value) where rgt_id is not null"
        );
    }
}
