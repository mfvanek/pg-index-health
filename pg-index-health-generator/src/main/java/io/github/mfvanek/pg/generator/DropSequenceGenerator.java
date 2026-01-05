/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.column.ColumnWithSerialType;

import java.util.Objects;

final class DropSequenceGenerator extends AbstractOptionsAwareSqlGenerator<ColumnWithSerialType> {

    DropSequenceGenerator(final GeneratingOptions options) {
        super(options);
    }

    @Override
    public String generate(final ColumnWithSerialType column) {
        Objects.requireNonNull(column, "column cannot be null");
        return keyword("drop sequence ") +
            keyword("if exists ") +
            column.getSequenceName() +
            ';';
    }
}
