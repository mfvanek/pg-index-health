/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.health.logger;

import io.github.mfvanek.pg.model.PgContext;

import javax.annotation.Nonnull;
import java.util.List;

public interface IndexesHealthLogger {

    @Nonnull
    List<String> logAll(@Nonnull Exclusions exclusions, @Nonnull PgContext pgContext);

    @Nonnull
    default List<String> logAll(@Nonnull final Exclusions exclusions) {
        return logAll(exclusions, PgContext.ofPublic());
    }
}
