/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import javax.annotation.Nonnull;

public interface PgHost {

    @Nonnull
    String getPgUrl();

    @Nonnull
    String getName();
}
