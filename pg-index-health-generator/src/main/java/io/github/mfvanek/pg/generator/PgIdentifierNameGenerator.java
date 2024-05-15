/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.generator.utils.NameUtils;
import io.github.mfvanek.pg.generator.utils.StringUtils;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.ForeignKey;

import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import static io.github.mfvanek.pg.generator.AbstractDbMigrationGenerator.DELIMITER_LENGTH;
import static io.github.mfvanek.pg.generator.PgIndexOnForeignKeyGenerator.MAX_IDENTIFIER_LENGTH;

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
        this.tableNameWithoutSchema = NameUtils.getTableNameWithoutSchema(foreignKey);
        this.columnsInIndex = foreignKey.getColumnsInConstraint().stream()
            .map(Column::getColumnName)
            .collect(Collectors.joining(AbstractDbMigrationGenerator.DELIMITER));
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
        int remainingLength = options.isNeedToAddIdx() ? MAX_IDENTIFIER_LENGTH - IDX.length() - DELIMITER_LENGTH : MAX_IDENTIFIER_LENGTH;
        final StringBuilder truncatedNameBuilder = new StringBuilder();
        final int mainPathLength = getMainPartLength();
        if (mainPathLength > remainingLength) {
            final int hash = columnsInIndex.hashCode(); // to make unique name
            final String columnsPart;
            if (hash < 0) {
                columnsPart = "n" + Math.abs(hash); // 'n' means 'negative'
            } else {
                columnsPart = String.valueOf(hash);
            }
            remainingLength = remainingLength - DELIMITER_LENGTH - columnsPart.length();
            truncatedNameBuilder.append(StringUtils.truncate(tableNameWithoutSchema, remainingLength))
                .append(AbstractDbMigrationGenerator.DELIMITER)
                .append(columnsPart);
            remainingLength -= tableNameWithoutSchema.length();
        } else {
            addMainPart(truncatedNameBuilder);
            remainingLength -= mainPathLength;
        }
        if (remainingLength > WITHOUT_NULLS.length()) {
            addWithoutNullsIfNeed(truncatedNameBuilder);
        }
        return addIdxIfNeed(truncatedNameBuilder)
            .toString();
    }

    private void addMainPart(@Nonnull final StringBuilder nameBuilder) {
        nameBuilder.append(tableNameWithoutSchema)
            .append(AbstractDbMigrationGenerator.DELIMITER)
            .append(columnsInIndex);
    }

    private int getMainPartLength() {
        return tableNameWithoutSchema.length() + DELIMITER_LENGTH + columnsInIndex.length();
    }

    private void addWithoutNullsIfNeed(@Nonnull final StringBuilder nameBuilder) {
        if (hasToAddWithoutNullsSuffix) {
            nameBuilder.append(AbstractDbMigrationGenerator.DELIMITER)
                .append(WITHOUT_NULLS);
        }
    }

    @Nonnull
    private StringBuilder addIdxIfNeed(@Nonnull final StringBuilder nameBuilder) {
        if (options.isNeedToAddIdx()) {
            if (options.getIdxPosition() == IdxPosition.SUFFIX) {
                nameBuilder.append(AbstractDbMigrationGenerator.DELIMITER)
                    .append(IDX);
            } else {
                nameBuilder.insert(0, IDX + AbstractDbMigrationGenerator.DELIMITER);
            }
        }
        return nameBuilder;
    }

    @Nonnull
    public static PgIdentifierNameGenerator of(@Nonnull final ForeignKey foreignKey, @Nonnull final GeneratingOptions options) {
        return new PgIdentifierNameGenerator(foreignKey, options);
    }
}
