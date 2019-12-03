/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

// TODO add test
public class PgHostImpl implements PgHost {

    private static final String MASTER = "master";
    private static final String REPLICA = "replica";

    private final String pgUrl;
    private final Set<String> hostNames;

    private PgHostImpl(@Nonnull final String pgUrl) {
        if (MASTER.equals(pgUrl) || REPLICA.equals(pgUrl)) {
            this.pgUrl = pgUrl;
            this.hostNames = Set.of(pgUrl);
        } else {
            this.pgUrl = PgConnectionValidators.pgUrlNotBlankAndValid(pgUrl, "pgUrl");
            this.hostNames = PgUrlParser.extractHostNames(pgUrl);
        }
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
        return new PgHostImpl(MASTER);
    }

    @Nonnull
    public static PgHost ofReplica() {
        return new PgHostImpl(REPLICA);
    }

    @Nonnull
    public static PgHost of(@Nonnull final String pgUrl) {
        return new PgHostImpl(pgUrl);
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
                '}';
    }
}
