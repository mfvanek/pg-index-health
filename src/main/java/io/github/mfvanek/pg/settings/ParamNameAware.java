/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.settings;

import javax.annotation.Nonnull;

@SuppressWarnings("WeakerAccess")
public interface ParamNameAware {

    @Nonnull
    String getName();
}
