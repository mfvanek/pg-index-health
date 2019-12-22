/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import com.mfvanek.pg.model.PgContext;

import javax.annotation.Nonnull;
import java.util.List;

public interface IndexesHealthLogger {

    @Nonnull
    List<String> logAll(@Nonnull PgContext pgContext);

    @Nonnull
    default List<String> logAll() {
        return logAll(PgContext.ofPublic());
    }
}
