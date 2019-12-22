/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.utils;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface DatabaseConfigurer {

    @Nonnull
    DatabasePopulator configure(@Nonnull DatabasePopulator databasePopulator);
}
