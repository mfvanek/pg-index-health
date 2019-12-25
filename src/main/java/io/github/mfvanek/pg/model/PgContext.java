/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

public class PgContext {

    private final String schemaName;

    private PgContext(@Nonnull final String schemaName) {
        this.schemaName = Validators.notBlank(schemaName, "schemaName");
    }

    @Nonnull
    public String getSchemaName() {
        return schemaName;
    }

    @Override
    public String toString() {
        return PgContext.class.getSimpleName() + '{' +
                "schemaName='" + schemaName + '\'' +
                '}';
    }

    public static PgContext of(@Nonnull final String schemeName) {
        return new PgContext(schemeName);
    }

    public static PgContext ofPublic() {
        return of("public");
    }
}
