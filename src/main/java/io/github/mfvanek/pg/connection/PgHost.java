/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;

/**
 * An abstraction of database host. This is a real server where your queries will be executed.
 */
public interface PgHost {

    @Nonnull
    String getPgUrl();

    @Nonnull
    String getName();
}
