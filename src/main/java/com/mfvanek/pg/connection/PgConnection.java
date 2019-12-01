/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.List;

public interface PgConnection {

    @Nonnull
    DataSource getMasterDataSource();

    @Nonnull
    List<? extends DataSource> getReplicasDataSource();

    int getReplicasCount();
}
