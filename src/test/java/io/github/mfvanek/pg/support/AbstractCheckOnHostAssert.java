/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.TableNameAware;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.assertj.core.util.CheckReturnValue;

import javax.annotation.Nonnull;

@SuppressWarnings({"PMD.LinguisticNaming", "checkstyle:AbstractClassName"})
public class AbstractCheckOnHostAssert<E extends DbObject & TableNameAware> extends AbstractAssert<AbstractCheckOnHostAssert<E>, DatabaseCheckOnHost<E>> {

    protected AbstractCheckOnHostAssert(@Nonnull final DatabaseCheckOnHost<E> abstractCheckOnHost) {
        super(abstractCheckOnHost, AbstractCheckOnHostAssert.class);
    }

    public <T> AbstractCheckOnHostAssert<E> hasType(@Nonnull final Class<T> type) {
        isNotNull();
        if (!actual.getType().isAssignableFrom(type)) {
            failWithMessage("Expected type %s but was %s", type, actual.getType());
        }
        return this;
    }

    public AbstractCheckOnHostAssert<E> hasDiagnostic(@Nonnull final Diagnostic diagnostic) {
        isNotNull();
        if (actual.getDiagnostic() != diagnostic) {
            failWithMessage("Expected diagnostic %s but was %s", diagnostic, actual.getDiagnostic());
        }
        return this;
    }

    public AbstractCheckOnHostAssert<E> hasHost(@Nonnull final PgHost host) {
        isNotNull();
        if (!actual.getHost().equals(host)) {
            failWithMessage("Expected host %s but was %s", host, actual.getHost());
        }
        return this;
    }

    @CheckReturnValue
    public ListAssert<E> executing() {
        isNotNull();
        return Assertions.assertThat(actual.check());
    }

    @CheckReturnValue
    public ListAssert<E> executing(@Nonnull final PgContext pgContext) {
        isNotNull();
        return Assertions.assertThat(actual.check(pgContext));
    }

    @CheckReturnValue
    public static <T extends DbObject & TableNameAware> AbstractCheckOnHostAssert<T> assertThat(@Nonnull final DatabaseCheckOnHost<T> actual) {
        return new AbstractCheckOnHostAssert<>(actual);
    }
}
