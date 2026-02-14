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

import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.Locale;
import java.util.Objects;

abstract class AbstractOptionsAwareSqlGenerator<T extends TableNameAware> {

    protected static final String WHITESPACE = " ";

    protected final GeneratingOptions options;

    protected AbstractOptionsAwareSqlGenerator(final GeneratingOptions options) {
        this.options = Objects.requireNonNull(options, "options cannot be null");
    }

    protected String keyword(final String keyword) {
        if (options.isUppercaseForKeywords()) {
            return keyword.toUpperCase(Locale.ROOT);
        }
        return keyword;
    }

    abstract String generate(T dbObject);
}
