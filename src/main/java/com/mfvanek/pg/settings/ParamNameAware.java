/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.settings;

import javax.annotation.Nonnull;

public interface ParamNameAware {

    @Nonnull
    String getName();
}
