/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.model.PgContext;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface TestExecutor {

    void execute(@Nonnull PgContext pgContext);
}
