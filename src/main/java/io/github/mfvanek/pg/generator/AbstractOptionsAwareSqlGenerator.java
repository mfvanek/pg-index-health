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

import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.utils.Locales;

import java.util.Objects;
import javax.annotation.Nonnull;

abstract class AbstractOptionsAwareSqlGenerator<T extends TableNameAware> {

    protected static final String WHITESPACE = " ";

    protected final GeneratingOptions options;

    protected AbstractOptionsAwareSqlGenerator(@Nonnull final GeneratingOptions options) {
        this.options = Objects.requireNonNull(options, "options cannot be null");
    }

    @Nonnull
    protected String keyword(@Nonnull final String keyword) {
        if (options.isUppercaseForKeywords()) {
            return keyword.toUpperCase(Locales.DEFAULT);
        }
        return keyword;
    }

    @Nonnull
    public abstract String generate(@Nonnull T dbObject);
}
