package com.mfvanek.pg.settings;

import javax.annotation.Nonnull;

public interface PgParam {

    @Nonnull
    String getName();

    @Nonnull
    String getValue();
}
