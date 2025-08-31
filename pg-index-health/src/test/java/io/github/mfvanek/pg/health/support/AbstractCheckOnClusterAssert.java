/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.support;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import org.assertj.core.annotation.CheckReturnValue;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.jspecify.annotations.NonNull;

import java.util.function.Predicate;

@SuppressWarnings({"PMD.LinguisticNaming", "checkstyle:AbstractClassName"})
public class AbstractCheckOnClusterAssert<E extends @NonNull DbObject> extends AbstractAssert<AbstractCheckOnClusterAssert<E>, DatabaseCheckOnCluster<E>> {

    protected AbstractCheckOnClusterAssert(final DatabaseCheckOnCluster<E> abstractCheckOnCluster) {
        super(abstractCheckOnCluster, AbstractCheckOnClusterAssert.class);
    }

    public <T> AbstractCheckOnClusterAssert<E> hasType(final Class<T> type) {
        isNotNull();
        if (!actual.getType().isAssignableFrom(type)) {
            failWithMessage("Expected type %s but was %s", type, actual.getType());
        }
        return this;
    }

    public AbstractCheckOnClusterAssert<E> hasDiagnostic(final Diagnostic diagnostic) {
        isNotNull();
        if (actual.getDiagnostic() != diagnostic) {
            failWithMessage("Expected diagnostic %s but was %s", diagnostic, actual.getDiagnostic());
        }
        return this;
    }

    public AbstractCheckOnClusterAssert<E> isStatic() {
        isNotNull();
        if (!actual.isStatic()) {
            failWithMessage("Expected diagnostic should be STATIC but was %s", actual.getDiagnostic());
        }
        return this;
    }

    public AbstractCheckOnClusterAssert<E> isRuntime() {
        isNotNull();
        if (!actual.isRuntime()) {
            failWithMessage("Expected diagnostic should be RUNTIME but was %s", actual.getDiagnostic());
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
    public static <T extends @NonNull DbObject> AbstractCheckOnClusterAssert<T> assertThat(final DatabaseCheckOnCluster<T> actual) {
        return new AbstractCheckOnClusterAssert<>(actual);
    }
}
