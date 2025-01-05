/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.ForeignKey;

import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * SQL query generator for creating an index covering given foreign key.
 *
 * @author Ivan Vahrushev
 * @since 0.5.0
 */
final class PgIndexOnForeignKeyGenerator extends AbstractOptionsAwareSqlGenerator<ForeignKey> {

    // See https://www.postgresql.org/docs/current/limits.html
    public static final int MAX_IDENTIFIER_LENGTH = 63;

    PgIndexOnForeignKeyGenerator(@Nonnull final GeneratingOptions options) {
        super(options);
    }

    @Nonnull
    @Override
    public String generate(@Nonnull final ForeignKey foreignKey) {
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
            .append(foreignKey.getColumnsInConstraint().stream().map(Column::getColumnName).collect(Collectors.joining(", ")))
            .append(')');
        if (hasToExcludeNulls(foreignKey)) {
            excludeNulls(queryBuilder, foreignKey);
        }
        return queryBuilder.append(';')
            .toString();
    }

    private void appendFullIndexNameAsComment(@Nonnull final StringBuilder queryBuilder, @Nonnull final String fullIndexName) {
        queryBuilder.append("/* ")
            .append(fullIndexName)
            .append(" */")
            .append(options.isBreakLines() ? System.lineSeparator() : " ");
    }

    private boolean hasToExcludeNulls(@Nonnull final ForeignKey foreignKey) {
        return options.isExcludeNulls() &&
            foreignKey.getColumnsInConstraint().stream().anyMatch(Column::isNullable);
    }

    private void excludeNulls(@Nonnull final StringBuilder queryBuilder, @Nonnull final ForeignKey foreignKey) {
        queryBuilder.append(keyword(" where "));
        final String columnsList = foreignKey.getColumnsInConstraint().stream()
            .filter(Column::isNullable)
            .map(Column::getColumnName)
            .map(n -> n + keyword(" is not null"))
            .collect(Collectors.joining(" and "));
        queryBuilder.append(columnsList);
    }
}
