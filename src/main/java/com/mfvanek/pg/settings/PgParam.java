/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.settings;

import javax.annotation.Nonnull;

public interface PgParam extends ParamNameAware {

    @Nonnull
    String getValue();
}
