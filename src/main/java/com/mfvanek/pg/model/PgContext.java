/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import com.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

public class PgContext {

    private final String schemeName;

    private PgContext(@Nonnull final String schemeName) {
        this.schemeName = Validators.notBlank(schemeName, "schemeName");
    }

    @Nonnull
    public String getSchemeName() {
        return schemeName;
    }

    @Override
    public String toString() {
        return PgContext.class.getSimpleName() + '{' +
                "schemeName='" + schemeName + '\'' +
                '}';
    }

    public static PgContext of(@Nonnull final String schemeName) {
        return new PgContext(schemeName);
    }

    public static PgContext ofPublic() {
        return of("public");
    }
}
