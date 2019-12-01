/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SimplePgConnection implements PgConnection {

    private final DataSource masterDataSource;
    private final List<DataSource> replicasDataSource;

    private SimplePgConnection(@Nonnull final DataSource masterDataSource,
                               @Nonnull final List<DataSource> replicasDataSource) {
        this.masterDataSource = Objects.requireNonNull(masterDataSource);
        this.replicasDataSource = List.copyOf(Objects.requireNonNull(replicasDataSource));
    }

    @Override
    @Nonnull
    public DataSource getMasterDataSource() {
        return masterDataSource;
    }

    @Override
    @Nonnull
    public List<DataSource> getReplicasDataSource() {
        return replicasDataSource;
    }

    @Override
    public int getReplicasCount() {
        return replicasDataSource.size();
    }

    public static PgConnection of(@Nonnull final DataSource masterDataSource) {
        return new SimplePgConnection(masterDataSource, Collections.emptyList());
    }

    public static PgConnection of(@Nonnull final DataSource masterDataSource,
                                  @Nonnull final DataSource replicaDataSource) {
        return new SimplePgConnection(masterDataSource, List.of(replicaDataSource));
    }

    public static PgConnection of(@Nonnull final DataSource masterDataSource,
                                  @Nonnull final List<DataSource> replicasDataSource) {
        return new SimplePgConnection(masterDataSource, replicasDataSource);
    }
}
