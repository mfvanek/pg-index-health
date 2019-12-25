/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
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
