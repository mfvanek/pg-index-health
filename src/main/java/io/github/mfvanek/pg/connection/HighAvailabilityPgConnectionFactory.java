/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
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
