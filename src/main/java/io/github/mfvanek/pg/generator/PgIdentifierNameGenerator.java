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
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import javax.annotation.Nonnull;

import static io.github.mfvanek.pg.generator.DbMigrationGeneratorImpl.DELIMITER;
import static io.github.mfvanek.pg.generator.PgIndexOnForeignKeyGenerator.MAX_IDENTIFIER_LENGTH;
import static java.util.stream.Collectors.joining;

/**
 * Index name generator.
 *
 * @author Ivan Vahrushev
 * @since 0.5.0
 */
class PgIdentifierNameGenerator {

    private static final String IDX = "idx";
    private static final String WITHOUT_NULLS = "without_nulls";

    private final GeneratingOptions options;
    private final String tableNameWithoutSchema;
    private final String columnsInIndex;
    private final boolean hasToAddWithoutNullsSuffix;

    private PgIdentifierNameGenerator(@Nonnull final ForeignKey foreignKey, @Nonnull final GeneratingOptions options) {
        Objects.requireNonNull(foreignKey, "foreignKey cannot be null");
        this.options = Objects.requireNonNull(options, "options cannot be null");
        this.tableNameWithoutSchema = getTableNameWithoutSchema(foreignKey);
        this.columnsInIndex = foreignKey.getColumnsInConstraint().stream()
                .map(Column::getColumnName)
                .collect(joining(DELIMITER));
        this.hasToAddWithoutNullsSuffix = options.isNameWithoutNulls() && options.isExcludeNulls() &&
                foreignKey.getColumnsInConstraint().stream().anyMatch(Column::isNullable);
    }

    @Nonnull
    public String generateFullIndexName() {
        final StringBuilder fullNameBuilder = new StringBuilder();
        addMainPart(fullNameBuilder);
        addWithoutNullsIfNeed(fullNameBuilder);
        return addIdxIfNeed(fullNameBuilder)
                .toString();
    }

    @Nonnull
    public String generateTruncatedIndexName() {
        int remainingLength = options.isNeedToAddIdx() ? MAX_IDENTIFIER_LENGTH - IDX.length() - DELIMITER.length() : MAX_IDENTIFIER_LENGTH;
        final StringBuilder truncatedNameBuilder = new StringBuilder();
        if (tableNameWithoutSchema.length() + DELIMITER.length() + columnsInIndex.length() > remainingLength) {
            final int hash = columnsInIndex.hashCode(); // to make unique name
            final String columnsPart;
            if (hash < 0) {
                columnsPart = "n" + Math.abs(hash); // 'n' means 'negative'
            } else {
                columnsPart = String.valueOf(hash);
            }
            remainingLength = remainingLength - DELIMITER.length() - columnsPart.length();
            truncatedNameBuilder.append(StringUtils.truncate(tableNameWithoutSchema, remainingLength))
                    .append(DELIMITER)
                    .append(columnsPart);
            remainingLength -= tableNameWithoutSchema.length();
        } else {
            addMainPart(truncatedNameBuilder);
        }
        if (remainingLength > WITHOUT_NULLS.length()) {
            addWithoutNullsIfNeed(truncatedNameBuilder);
        }
        return addIdxIfNeed(truncatedNameBuilder)
                .toString();
    }

    private void addMainPart(@Nonnull final StringBuilder nameBuilder) {
        nameBuilder.append(tableNameWithoutSchema)
                .append(DELIMITER)
                .append(columnsInIndex);
    }

    private void addWithoutNullsIfNeed(@Nonnull final StringBuilder nameBuilder) {
        if (hasToAddWithoutNullsSuffix) {
            nameBuilder.append(DELIMITER)
                    .append(WITHOUT_NULLS);
        }
    }

    @Nonnull
    private StringBuilder addIdxIfNeed(@Nonnull final StringBuilder nameBuilder) {
        if (options.isNeedToAddIdx()) {
            if (options.getIdxPosition() == IdxPosition.SUFFIX) {
                nameBuilder.append(DELIMITER)
                        .append(IDX);
            } else {
                nameBuilder.insert(0, IDX + DELIMITER);
            }
        }
        return nameBuilder;
    }

    @Nonnull
    private static String getTableNameWithoutSchema(@Nonnull final ForeignKey foreignKey) {
        final String tableName = foreignKey.getTableName();
        final int index = tableName.indexOf('.');
        final boolean containsSchema = index >= 0;
        if (containsSchema) {
            return tableName.substring(index + 1);
        }
        return tableName;
    }

    @Nonnull
    public static PgIdentifierNameGenerator of(@Nonnull final ForeignKey foreignKey, @Nonnull final GeneratingOptions options) {
        return new PgIdentifierNameGenerator(foreignKey, options);
    }
}
