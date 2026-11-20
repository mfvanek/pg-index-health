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
 * Represents a database statement that adds blank comments on tables and their columns.
 */
public class AddBlankCommentOnTablesAndColumnsStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code AddBlankCommentOnTablesAndColumnsStatement}.
     */
    public AddBlankCommentOnTablesAndColumnsStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "comment on table {schemaName}.clients is '   ';",
            "comment on table {schemaName}.accounts is '';",
            "comment on column {schemaName}.clients.id is '';",
            "comment on column {schemaName}.accounts.id is '   ';"
        );
    }
}
