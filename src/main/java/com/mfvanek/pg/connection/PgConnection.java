/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

public class PgConnection {

    private final DataSource masterDataSource;
    private final List<DataSource> replicasDataSource;

    private PgConnection(@Nonnull final DataSource masterDataSource, @Nonnull final List<DataSource> replicasDataSource) {
        this.masterDataSource = Objects.requireNonNull(masterDataSource);
        this.replicasDataSource = List.copyOf(Objects.requireNonNull(replicasDataSource));
    }

    @Nonnull
    public DataSource getMasterDataSource() {
        return masterDataSource;
    }

    @Nonnull
    public List<DataSource> getReplicasDataSource() {
        return replicasDataSource;
    }

    public static PgConnection of(@Nonnull final DataSource masterDataSource,
                                  @Nonnull final List<DataSource> replicasDataSource) {
        return new PgConnection(masterDataSource, replicasDataSource);
    }
}
