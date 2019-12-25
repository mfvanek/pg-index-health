/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.index.health.logger;

import javax.annotation.Nonnull;

@SuppressWarnings("WeakerAccess")
public interface LoggingKey {

    @Nonnull
    String getKeyName();

    @Nonnull
    String getSubKeyName();
}
