/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.support;

import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import org.assertj.core.annotation.CheckReturnValue;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.jspecify.annotations.NonNull;

import java.util.function.Predicate;

@SuppressWarnings({"PMD.LinguisticNaming", "checkstyle:AbstractClassName"})
public class AbstractCheckOnHostAssert<E extends @NonNull DbObject> extends AbstractAssert<AbstractCheckOnHostAssert<E>, DatabaseCheckOnHost<E>> {

    protected AbstractCheckOnHostAssert(final DatabaseCheckOnHost<E> abstractCheckOnHost) {
        super(abstractCheckOnHost, AbstractCheckOnHostAssert.class);
    }

    public <T> AbstractCheckOnHostAssert<E> hasType(final Class<T> type) {
        isNotNull();
        if (!actual.getType().isAssignableFrom(type)) {
            failWithMessage("Expected type %s but was %s", type, actual.getType());
        }
        return this;
    }

    public AbstractCheckOnHostAssert<E> hasDiagnostic(final Diagnostic diagnostic) {
        isNotNull();
        if (diagnostic.getName().equals(actual.getName())) {
            failWithMessage("Expected diagnostic %s but was %s", diagnostic, actual.getName());
        }
        return this;
    }

    public AbstractCheckOnHostAssert<E> hasHost(final PgHost host) {
        isNotNull();
        if (!actual.getHost().equals(host)) {
            failWithMessage("Expected host %s but was %s", host, actual.getHost());
        }
        return this;
    }

    public AbstractCheckOnHostAssert<E> isStatic() {
        isNotNull();
        if (!actual.isStatic()) {
            failWithMessage("Expected diagnostic should be STATIC but was %s", actual.isStatic());
        }
        return this;
    }

    public AbstractCheckOnHostAssert<E> isRuntime() {
        isNotNull();
        if (!actual.isRuntime()) {
            failWithMessage("Expected diagnostic should be RUNTIME but was %s", actual.isRuntime());
        }
        return this;
    }

    @CheckReturnValue
    public ListAssert<E> executing() {
        isNotNull();
        return Assertions.assertThat(actual.check());
    }

    @CheckReturnValue
    public ListAssert<E> executing(final PgContext pgContext) {
        isNotNull();
        return Assertions.assertThat(actual.check(pgContext));
    }

    @CheckReturnValue
    public ListAssert<E> executing(final PgContext pgContext, final Predicate<? super E> exclusionsFilter) {
        isNotNull();
        return Assertions.assertThat(actual.check(pgContext, exclusionsFilter));
    }

    @CheckReturnValue
    public static <T extends @NonNull DbObject> AbstractCheckOnHostAssert<T> assertThat(final DatabaseCheckOnHost<T> actual) {
        return new AbstractCheckOnHostAssert<>(actual);
    }
}
