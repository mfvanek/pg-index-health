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
 * Represents a database statement that creates an unlogged sequence.
 */
public class CreateUnloggedSequenceStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateUnloggedSequenceStatement}.
     */
    public CreateUnloggedSequenceStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create unlogged sequence {schemaName}.unlogged_seq_1;"
        );
    }
}
