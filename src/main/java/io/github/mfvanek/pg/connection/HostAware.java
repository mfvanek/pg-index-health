/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;

public interface HostAware {

    @Nonnull
    PgHost getHost();
}
