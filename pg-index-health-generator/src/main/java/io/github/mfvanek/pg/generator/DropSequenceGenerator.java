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

import io.github.mfvanek.pg.model.column.ColumnWithSerialType;

import java.util.Objects;
import javax.annotation.Nonnull;

final class DropSequenceGenerator extends AbstractOptionsAwareSqlGenerator<ColumnWithSerialType> {

    DropSequenceGenerator(@Nonnull final GeneratingOptions options) {
        super(options);
    }

    @Nonnull
    @Override
    public String generate(@Nonnull final ColumnWithSerialType column) {
        Objects.requireNonNull(column, "column cannot be null");
        return keyword("drop sequence ") +
                keyword("if exists ") +
                column.getSequenceName() +
                ';';
    }
}
