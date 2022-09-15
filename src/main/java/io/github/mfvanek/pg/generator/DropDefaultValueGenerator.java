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

import io.github.mfvanek.pg.model.table.ColumnNameAware;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * SQL query generator for dropping a default value on given column.
 *
 * @author Ivan Vahrushev
 * @since 0.6.2
 */
final class DropDefaultValueGenerator extends AbstractOptionsAwareSqlGenerator<ColumnNameAware> {

    DropDefaultValueGenerator(@Nonnull final GeneratingOptions options) {
        super(options);
    }

    @Nonnull
    @Override
    public String generate(@Nonnull final ColumnNameAware column) {
        Objects.requireNonNull(column, "column cannot be null");
        return keyword("alter table ") +
                keyword("if exists ") +
                column.getTableName() +
                (options.isBreakLines() ? System.lineSeparator() : " ") +
                (options.isBreakLines() ? StringUtils.repeat(' ', options.getIndentation()) : "") +
                keyword("alter column ") +
                column.getColumnName() +
                keyword(" drop default") +
                ';';
    }
}
