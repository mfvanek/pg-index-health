/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;

public interface HighAvailabilityPgConnectionFactory {

    @Nonnull
    HighAvailabilityPgConnection of(@Nonnull String writeUrl,
                                    @Nonnull String userName,
                                    @Nonnull String password);

    @Nonnull
    HighAvailabilityPgConnection of(@Nonnull String writeUrl,
                                    @Nonnull String userName,
                                    @Nonnull String password,
                                    @Nonnull String readUrl);

    @Nonnull
    HighAvailabilityPgConnection of(@Nonnull String writeUrl,
                                    @Nonnull String userName,
                                    @Nonnull String password,
                                    @Nonnull String readUrl,
                                    @Nonnull String cascadeAsyncReadUrl);
}
