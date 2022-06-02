/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.table.Column;
import io.github.mfvanek.pg.utils.Locales;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * SQL query generator for creating an index covering given foreign key.
 *
 * @author Ivan Vahrushev
 * @since 0.5.0
 */
public class PgIndexOnForeignKeyGenerator {

    // See https://www.postgresql.org/docs/current/limits.html
    public static final int MAX_IDENTIFIER_LENGTH = 63;

    private final ForeignKey foreignKey;
    private final GeneratingOptions options;
    private final PgIdentifierNameGenerator nameGenerator;

    private PgIndexOnForeignKeyGenerator(@Nonnull final ForeignKey foreignKey, @Nonnull final GeneratingOptions options) {
        this.foreignKey = Objects.requireNonNull(foreignKey, "foreignKey cannot be null");
        this.options = Objects.requireNonNull(options, "options cannot be null");
        this.nameGenerator = PgIdentifierNameGenerator.of(foreignKey, options);
    }

    @Nonnull
    public String generate() {
        final StringBuilder queryBuilder = new StringBuilder();
        final String fullIndexName = nameGenerator.generateFullIndexName();
        final boolean hasToTruncate = fullIndexName.length() > MAX_IDENTIFIER_LENGTH;
        if (hasToTruncate) {
            queryBuilder.append("/* ")
                    .append(fullIndexName)
                    .append(" */")
                    .append(options.isBreakLines() ? System.lineSeparator() : " ");
        }
        queryBuilder.append(keyword("create index "))
                .append(options.isConcurrently() ? keyword("concurrently ") : "")
                .append(keyword("if not exists "))
                .append(hasToTruncate ? nameGenerator.generateTruncatedIndexName() : fullIndexName)
                .append(options.isBreakLines() ? System.lineSeparator() : " ")
                .append(options.isBreakLines() ? StringUtils.repeat(' ', options.getIndentation()) : "")
                .append(keyword("on "))
                .append(foreignKey.getTableName())
                .append(" (")
                .append(foreignKey.getColumnsInConstraint().stream().map(Column::getColumnName).collect(Collectors.joining(", ")))
                .append(')');
        final boolean hasToExcludeNulls = options.isExcludeNulls() &&
                foreignKey.getColumnsInConstraint().stream().anyMatch(Column::isNullable);
        if (hasToExcludeNulls) {
            queryBuilder.append(keyword(" where "));
            final String columnsList = foreignKey.getColumnsInConstraint().stream()
                    .filter(Column::isNullable)
                    .map(Column::getColumnName)
                    .map(n -> n + keyword(" is not null"))
                    .collect(Collectors.joining(" and "));
            queryBuilder.append(columnsList);
        }
        return queryBuilder.append(';').toString();
    }

    @Nonnull
    private String keyword(@Nonnull final String keyword) {
        if (options.isUppercaseForKeywords()) {
            return keyword.toUpperCase(Locales.DEFAULT);
        }
        return keyword;
    }

    @Nonnull
    public static PgIndexOnForeignKeyGenerator of(@Nonnull final ForeignKey foreignKey, @Nonnull final GeneratingOptions options) {
        return new PgIndexOnForeignKeyGenerator(foreignKey, options);
    }
}
