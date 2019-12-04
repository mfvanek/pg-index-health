/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

public class PgHostImpl implements PgHost {

    private final String pgUrl;
    private final Set<String> hostNames;

    private PgHostImpl(@Nonnull final String pgUrl, boolean withValidation) {
        this.pgUrl = PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, "pgUrl");
        this.hostNames = PgUrlParser.extractHostNames(pgUrl);
    }

    private PgHostImpl(@Nonnull final String hostName) {
        this.hostNames = Set.of(Objects.requireNonNull(hostName));
        this.pgUrl = PgUrlParser.URL_HEADER + hostName;
    }

    @Nonnull
    @Override
    public String getPgUrl() {
        return pgUrl;
    }

    @Nonnull
    @Override
    public String getName() {
        return hostNames.size() == 1 ?
                hostNames.iterator().next() :
                "One of " + hostNames;
    }

    @Nonnull
    public static PgHost ofMaster() {
        return new PgHostImpl("master");
    }

    @Nonnull
    public static PgHost ofUrl(@Nonnull final String pgUrl) {
        return new PgHostImpl(pgUrl, true);
    }

    @Nonnull
    public static PgHost ofName(@Nonnull final String hostName) {
        return new PgHostImpl(hostName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PgHostImpl pgHost = (PgHostImpl) o;
        return hostNames.equals(pgHost.hostNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostNames);
    }

    @Override
    public String toString() {
        return PgHostImpl.class.getSimpleName() + '{' +
                "pgUrl='" + pgUrl + '\'' +
                ", hostNames=" + hostNames +
                '}';
    }
}
