/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import javax.annotation.Nonnull;

public interface LoggingKey {

    @Nonnull
    String getKeyName();

    @Nonnull
    String getSubKeyName();
}
