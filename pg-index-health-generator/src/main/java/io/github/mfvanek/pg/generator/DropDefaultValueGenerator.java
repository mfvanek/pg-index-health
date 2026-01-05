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

import io.github.mfvanek.pg.model.column.ColumnNameAware;

import java.util.Objects;

/**
 * SQL query generator for dropping a default value on given column.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.2
 */
final class DropDefaultValueGenerator extends AbstractOptionsAwareSqlGenerator<ColumnNameAware> {

    DropDefaultValueGenerator(final GeneratingOptions options) {
        super(options);
    }

    @Override
    public String generate(final ColumnNameAware column) {
        Objects.requireNonNull(column, "column cannot be null");
        return keyword("alter table ") +
            keyword("if exists ") +
            column.getTableName() +
            (options.isBreakLines() ? System.lineSeparator() : " ") +
            (options.isBreakLines() ? WHITESPACE.repeat(options.getIndentation()) : "") +
            keyword("alter column ") +
            column.getColumnName() +
            keyword(" drop default") +
            ';';
    }
}
