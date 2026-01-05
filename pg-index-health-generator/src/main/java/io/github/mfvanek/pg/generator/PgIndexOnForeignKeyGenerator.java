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
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.constraint.ForeignKey;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * SQL query generator for creating an index covering given foreign key.
 *
 * @author Ivan Vakhrushev
 * @since 0.5.0
 */
final class PgIndexOnForeignKeyGenerator extends AbstractOptionsAwareSqlGenerator<ForeignKey> {

    // See https://www.postgresql.org/docs/current/limits.html
    public static final int MAX_IDENTIFIER_LENGTH = 63;

    PgIndexOnForeignKeyGenerator(final GeneratingOptions options) {
        super(options);
    }

    @Override
    public String generate(final ForeignKey foreignKey) {
        Objects.requireNonNull(foreignKey, "foreignKey cannot be null");
        final PgIdentifierNameGenerator nameGenerator = PgIdentifierNameGenerator.of(foreignKey, options);
        final StringBuilder queryBuilder = new StringBuilder();
        final String fullIndexName = nameGenerator.generateFullIndexName();
        final boolean hasToTruncate = fullIndexName.length() > MAX_IDENTIFIER_LENGTH;
        if (hasToTruncate) {
            appendFullIndexNameAsComment(queryBuilder, fullIndexName);
        }
        queryBuilder.append(keyword("create index "))
            .append(options.isConcurrently() ? keyword("concurrently ") : "")
            .append(keyword("if not exists "))
            .append(hasToTruncate ? nameGenerator.generateTruncatedIndexName() : fullIndexName)
            .append(options.isBreakLines() ? System.lineSeparator() : " ")
            .append(options.isBreakLines() ? WHITESPACE.repeat(options.getIndentation()) : "")
            .append(keyword("on "))
            .append(foreignKey.getTableName())
            .append(" (")
            .append(foreignKey.getColumns().stream().map(ColumnNameAware::getColumnName).collect(Collectors.joining(", ")))
            .append(')');
        if (hasToExcludeNulls(foreignKey)) {
            excludeNulls(queryBuilder, foreignKey);
        }
        return queryBuilder.append(';')
            .toString();
    }

    private void appendFullIndexNameAsComment(final StringBuilder queryBuilder, final String fullIndexName) {
        queryBuilder.append("/* ")
            .append(fullIndexName)
            .append(" */")
            .append(options.isBreakLines() ? System.lineSeparator() : " ");
    }

    private boolean hasToExcludeNulls(final ColumnsAware foreignKey) {
        return options.isExcludeNulls() &&
            foreignKey.getColumns().stream().anyMatch(ColumnNameAware::isNullable);
    }

    private void excludeNulls(final StringBuilder queryBuilder, final ColumnsAware foreignKey) {
        queryBuilder.append(keyword(" where "));
        final String columnsList = foreignKey.getColumns().stream()
            .filter(ColumnNameAware::isNullable)
            .map(ColumnNameAware::getColumnName)
            .map(n -> n + keyword(" is not null"))
            .collect(Collectors.joining(" and "));
        queryBuilder.append(columnsList);
    }
}
